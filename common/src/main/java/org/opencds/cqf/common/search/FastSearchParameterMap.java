package org.opencds.cqf.common.search;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.util.ObjectUtil;
import ca.uhn.fhir.util.UrlUtil;

public class FastSearchParameterMap extends SearchParameterMap {


    public static FastSearchParameterMap newSynchronous() {
		FastSearchParameterMap retVal = new FastSearchParameterMap();
		retVal.setLoadSynchronous(true);
		return retVal;
	}

	public static FastSearchParameterMap newSynchronous(String theName, IQueryParameterType theParam) {
		FastSearchParameterMap retVal = new FastSearchParameterMap();
		retVal.setLoadSynchronous(true);
		retVal.add(theName, theParam);
		return retVal;
	}

    public String toNormalizedQueryString(FhirContext theCtx) {
		StringBuilder b = new StringBuilder();

		ArrayList<String> keys = new ArrayList<>(keySet());
		Collections.sort(keys);

		Map<IQueryParameterType, String> stringCache = new HashMap<>();

		for (String nextKey : keys) {

			List<List<IQueryParameterType>> nextValuesAndsIn = get(nextKey);
			List<List<IQueryParameterType>> nextValuesAndsOut = new ArrayList<>();

			for (List<? extends IQueryParameterType> nextValuesAndIn : nextValuesAndsIn) {

				List<IQueryParameterType> nextValuesOrsOut = new ArrayList<>();
				for (IQueryParameterType nextValueOrIn : nextValuesAndIn) {
					if (nextValueOrIn.getMissing() != null || isNotBlank(stringCache.computeIfAbsent(nextValueOrIn, k -> k.getValueAsQueryToken(theCtx)))) {
						nextValuesOrsOut.add(nextValueOrIn);
					}
				}

				nextValuesOrsOut.sort(new QueryParameterTypeComparator(theCtx, stringCache));

				if (nextValuesOrsOut.size() > 0) {
					nextValuesAndsOut.add(nextValuesOrsOut);
				}

			} // for AND

			nextValuesAndsOut.sort(new QueryParameterOrComparator(theCtx, stringCache));

			for (List<IQueryParameterType> nextValuesAnd : nextValuesAndsOut) {
				addUrlParamSeparator(b);
				IQueryParameterType firstValue = nextValuesAnd.get(0);
				b.append(UrlUtil.escapeUrlParam(nextKey));

				if (firstValue.getMissing() != null) {
					b.append(Constants.PARAMQUALIFIER_MISSING);
					b.append('=');
					if (firstValue.getMissing()) {
						b.append(Constants.PARAMQUALIFIER_MISSING_TRUE);
					} else {
						b.append(Constants.PARAMQUALIFIER_MISSING_FALSE);
					}
					continue;
				}

				if (isNotBlank(firstValue.getQueryParameterQualifier())) {
					b.append(firstValue.getQueryParameterQualifier());
				}

				b.append('=');

				for (int i = 0; i < nextValuesAnd.size(); i++) {
					IQueryParameterType nextValueOr = nextValuesAnd.get(i);
					if (i > 0) {
						b.append(',');
					}
					String valueAsQueryToken = stringCache.computeIfAbsent(nextValueOr, k -> k.getValueAsQueryToken(theCtx));
					b.append(UrlUtil.escapeUrlParam(valueAsQueryToken));
				}
			}

		} // for keys

		SortSpec sort = getSort();
		boolean first = true;
		while (sort != null) {

			if (isNotBlank(sort.getParamName())) {
				if (first) {
					addUrlParamSeparator(b);
					b.append(Constants.PARAM_SORT);
					b.append('=');
					first = false;
				} else {
					b.append(',');
				}
				if (sort.getOrder() == SortOrderEnum.DESC) {
					b.append('-');
				}
				b.append(sort.getParamName());
			}

			Validate.isTrue(sort != sort.getChain()); // just in case, shouldn't happen
			sort = sort.getChain();
		}

		if (hasIncludes()) {
			addUrlIncludeParams(b, Constants.PARAM_INCLUDE, getIncludes());
		}
		addUrlIncludeParams(b, Constants.PARAM_REVINCLUDE, getRevIncludes());

		if (getLastUpdated() != null) {
			DateParam lb = getLastUpdated().getLowerBound();
			addLastUpdateParam(b, lb);
			DateParam ub = getLastUpdated().getUpperBound();
			addLastUpdateParam(b, ub);
		}

		if (getCount() != null) {
			addUrlParamSeparator(b);
			b.append(Constants.PARAM_COUNT);
			b.append('=');
			b.append(getCount());
		}

		if (getOffset() != null) {
			addUrlParamSeparator(b);
			b.append(Constants.PARAM_OFFSET);
			b.append('=');
			b.append(getOffset());
		}

		// Summary mode (_summary)
		if (getSummaryMode() != null) {
			addUrlParamSeparator(b);
			b.append(Constants.PARAM_SUMMARY);
			b.append('=');
			b.append(getSummaryMode().getCode());
		}

		// Search count mode (_total)
		if (getSearchTotalMode() != null) {
			addUrlParamSeparator(b);
			b.append(Constants.PARAM_SEARCH_TOTAL_MODE);
			b.append('=');
			b.append(getSearchTotalMode().getCode());
		}

		if (b.length() == 0) {
			b.append('?');
		}

		return b.toString();
	}

    private void addUrlParamSeparator(StringBuilder theB) {
		if (theB.length() == 0) {
			theB.append('?');
		} else {
			theB.append('&');
		}
	}

    private void addUrlIncludeParams(StringBuilder b, String paramName, Set<Include> theList) {
		ArrayList<Include> list = new ArrayList<>(theList);

		list.sort(new IncludeComparator());
		for (Include nextInclude : list) {
			addUrlParamSeparator(b);
			b.append(paramName);
			b.append('=');
			b.append(UrlUtil.escapeUrlParam(nextInclude.getParamType()));
			b.append(':');
			b.append(UrlUtil.escapeUrlParam(nextInclude.getParamName()));
			if (isNotBlank(nextInclude.getParamTargetType())) {
				b.append(':');
				b.append(nextInclude.getParamTargetType());
			}
		}
	}

    private void addLastUpdateParam(StringBuilder b, DateParam date) {
		if (date != null && isNotBlank(date.getValueAsString())) {
			addUrlParamSeparator(b);
			b.append(Constants.PARAM_LASTUPDATED);
			b.append('=');
			b.append(date.getValueAsString());
		}
	}

	public static class QueryParameterOrComparator implements Comparator<List<IQueryParameterType>> {
		private final FhirContext myCtx;
		private final Map<IQueryParameterType, String> stringCache;

		QueryParameterOrComparator(FhirContext theCtx, Map<IQueryParameterType, String> stringCache) {
			myCtx = theCtx;
			this.stringCache = stringCache;
		}

		@Override
		public int compare(List<IQueryParameterType> theO1, List<IQueryParameterType> theO2) {
			// These lists will never be empty
			return FastSearchParameterMap.compare(myCtx, theO1.get(0), theO2.get(0), stringCache);
		}

	}

	public static class QueryParameterTypeComparator implements Comparator<IQueryParameterType> {

		private final FhirContext myCtx;
		private final Map<IQueryParameterType, String> stringCache;

		QueryParameterTypeComparator(FhirContext theCtx, Map<IQueryParameterType, String> stringCache) {
			myCtx = theCtx;
			this.stringCache = stringCache;
		}

		@Override
		public int compare(IQueryParameterType theO1, IQueryParameterType theO2) {
			return FastSearchParameterMap.compare(myCtx, theO1, theO2, stringCache);
		}

	}

	private static int compare(FhirContext theCtx, IQueryParameterType theO1, IQueryParameterType theO2, Map<IQueryParameterType, String> stringCache) {
		int retVal;
		if (theO1.getMissing() == null && theO2.getMissing() == null) {
			retVal = 0;
		} else if (theO1.getMissing() == null) {
			retVal = -1;
		} else if (theO2.getMissing() == null) {
			retVal = 1;
		} else if (ObjectUtil.equals(theO1.getMissing(), theO2.getMissing())) {
			retVal = 0;
		} else {
			if (theO1.getMissing()) {
				retVal = 1;
			} else {
				retVal = -1;
			}
		}

		if (retVal == 0) {
			String q1 = theO1.getQueryParameterQualifier();
			String q2 = theO2.getQueryParameterQualifier();
			retVal = StringUtils.compare(q1, q2);
		}

		if (retVal == 0) {
			String v1 = stringCache.computeIfAbsent(theO1, k -> k.getValueAsQueryToken(theCtx));
			String v2 = stringCache.computeIfAbsent(theO2, k -> k.getValueAsQueryToken(theCtx));
			retVal = StringUtils.compare(v1, v2);
		}
		return retVal;
	}
}
