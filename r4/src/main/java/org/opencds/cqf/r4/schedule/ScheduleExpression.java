package org.opencds.cqf.r4.schedule;

import org.opencds.cqf.r4.schedule.RulerScheduler.ScheduleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm

public class ScheduleExpression {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleExpression.class);

    public RulerScheduler.ScheduleType scheduleType;

    private StringBuilder expression;

    private Date startDate;

    private int repeatCount;

    private int interValInSeconds;

   // private

    public ScheduleExpression(RulerScheduler.ScheduleType schType){
        scheduleType = schType;
        if(schType.equals(ScheduleType.CRON)){
            expression = new StringBuilder();
        }
    }

    public String getScheduleExpression(){
        return expression.toString();
    }

    public void setExpression(String expression) {
        this.expression.append(expression);
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public void setStartDate(String startDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.startDate = sdf.parse(startDate);
        } catch (ParseException e) {
            logger.info("Parse Exception:"+e.toString());
        }
    }

    public void setStartDateToNow(){
        startDate = new Date();
    }

    public int getInterValInSeconds() {
        return interValInSeconds;
    }

    public void setInterValInSeconds(int interValInSeconds) {
        this.interValInSeconds = interValInSeconds;
    }

    public void setInterval(int interval, String unit) {    // todo have to incorporate days / weeks / months
        if(unit.equalsIgnoreCase("h"))
            this.interValInSeconds = interval * 3600;
        else if(unit.equalsIgnoreCase("m")){
            this.interValInSeconds = interval * 60;
        }
        else if(unit.equalsIgnoreCase("s")){
            this.interValInSeconds = interval ;
        } else {
            this.interValInSeconds  = 15;
        }

    }

}
