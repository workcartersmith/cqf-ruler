package org.opencds.cqf.ruler;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * This class is just for debugging Spring config.
 */
// @Component
class BeanFinder implements BeanDefinitionRegistryPostProcessor {
	private final static Logger log = org.slf4j.LoggerFactory.getLogger(BeanFinder.class);

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory arg0) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		String[] beans = registry.getBeanDefinitionNames();
		for (String bean : beans) {
			BeanDefinition javaConfigBeanDefinition = registry.getBeanDefinition(bean);
			String description = javaConfigBeanDefinition.getResourceDescription();

			log.error("Bean: {} - Description: {}", bean, description);
		}
	}
}
