package org.opencds.cqf.r4.schedule;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Timing;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class RulerScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RulerScheduler.class);

    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

    public static Set<String> taskNames = new HashSet<String>();
    public static Map<String,BaseTaskJob> jobs = new HashMap<String, BaseTaskJob>();


    private JobDetail jobDetail;

    private Trigger trigger;

    private BaseTaskJob taskJob;

    private String group;

    private ScheduleExpression scheduleExpression;

    public RulerScheduler(BaseTaskJob job, String group) throws SchedulerException {
        this.taskJob = job;
        this.group = group;
        taskJob.setRulerScheduler(this);
        initiateTask();
        initScheduleExpression();
        init();
    }

    private void initScheduleExpression(){
        if(taskJob.getTiming() == null){
            scheduleExpression = new ScheduleExpression(ScheduleType.ONCE);
        } else{
            logger.info("The task should repeat.");
            scheduleExpression = new ScheduleExpression(RulerScheduler.ScheduleType.SIMPLE);
            scheduleExpression.setStartDateToNow(); //Set to Encounter or related Task completion time
            //scheduleExpression.setStartDate()
          //  scheduleExpression.setRepeatCount(3);
            scheduleExpression.setRepeatCount(taskJob.getTiming().getRepeat().getCount());
            //scheduleExpression.setInterValInSeconds(5);
            scheduleExpression.setInterval(taskJob.getTiming().getRepeat().getPeriod().intValue(),
                                            taskJob.getTiming().getRepeat().getPeriodUnit().toCode());

        }
    }

    private void initiateTask() throws FHIRException {

        if(taskJob == null ){
            throw new FHIRException("The task must not be null");
        }

        if(taskJob.getId()== null || taskJob.getId().isEmpty()){
            throw new FHIRException("The task must have an id.");
        }
        if(taskNames.contains(taskJob.getId())){
            throw new FHIRException("A task with same ID already exists");
        }
        taskNames.add(taskJob.getId());
        jobs.put(taskJob.getId(),taskJob);

    }

    private  void init() throws SchedulerException {
        initScheduler();
        initJobDetail();
        initTrigger();
    }

    private void initScheduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
    }

    public ScheduleExpression getScheduleExpression() {
        return scheduleExpression;
    }

    public void setScheduleExpression(ScheduleExpression scheduleExpression) {
        this.scheduleExpression = scheduleExpression;
    }

    private void initJobDetail(){
        jobDetail = JobBuilder.newJob(this.taskJob.getClass())
                .withIdentity(this.taskJob.getId(), group)
                .withDescription(taskJob.getId())
                .build();
    }

    private void initTrigger(){
        logger.info("SCH TYPE:"+scheduleExpression.getScheduleType());
        if(scheduleExpression.getScheduleType().equals(ScheduleType.ONCE)){
            trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                        .withIdentity("onceTrigger", group)
                        .startNow()                                //.startAt(scheduleExpression.getStartDate())
                        .forJob(taskJob.getId(), group)
                        .build();


        } else if(scheduleExpression.getScheduleType().equals(ScheduleType.SIMPLE)){
            logger.info("Interval:"+scheduleExpression.getInterValInSeconds());
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity("simpleTrigger", group)
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(scheduleExpression.getInterValInSeconds())
                            .repeatForever())
                    .build();

        } else if(scheduleExpression.getScheduleType().equals(ScheduleType.CRON)){   //scheduleParam ex: "0 0/2 8-17 * * ?"
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity("cronTrigger", group)
                    .withSchedule(CronScheduleBuilder.cronSchedule(scheduleExpression.getScheduleExpression()))
                    .forJob(taskJob.getId(), group)
                    .build();
        }
    }

    public void start(){
        try {
            scheduler.start();
            scheduler.scheduleJob(jobDetail, trigger);
        }catch (SchedulerException e) {
            logger.info("Job failed due to exception:"+ taskJob.getId()+"|"+group+"|"+e.toString());
        }
    }

    public void shutdown(){
        try {
            scheduler.shutdown(false);
            taskNames.remove(taskJob.getId());
            jobs.remove(taskJob.getId());
        } catch (SchedulerException e) {
            logger.info("Job has been shutdown:"+ taskJob.getId()+"|"+group+"|"+e.toString());
        }
    }


    public static enum ScheduleType {
        ONCE,
        SIMPLE,
        CRON
    }
}
