package org.opencds.cqf.r4.schedule;

import ca.uhn.fhir.jpa.model.sched.HapiJob;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskIntent;
import org.hl7.fhir.r4.model.Task.TaskPriority;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.hl7.fhir.r4.model.Timing;
import org.hl7.fhir.r4.model.Type;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseTaskJob implements HapiJob{

    private static final Logger logger = LoggerFactory.getLogger(BaseTaskJob.class);

    private Task task;

    private Timing timing = null;

    private RulerScheduler rulerScheduler;

    private String id = "";


    public BaseTaskJob(Task task) {
        this.task = task;
        this.id = task.getId();
        initTiming();
    }

    public BaseTaskJob(){
    }

    private void initTiming(){
        logger.info("Checking for timing extension.");
        if(task.hasExtension("http://hl7.org/fhir/aphl/StructureDefinition/timing")){
      //  if(task.hasExtension()){
            timing = new Timing();
            Extension extension = task.getExtensionByUrl("http://hl7.org/fhir/aphl/StructureDefinition/timing");
            if(extension != null){
                Type timingType = extension.getValue();
                if(timingType instanceof  Timing){
                    timing = (Timing)timingType;
                    logger.info("Timing is set.");
                    System.out.println("Timing is set.");
                }
            }
        }else{
            logger.info("No extension.");
        }
    }

    public void setTask(Task task) {
        this.task = task;
        this.id = task.getId();
    }
    public Task getTask() {
        return task;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RulerScheduler getRulerScheduler() {
        return rulerScheduler;
    }

    public void setRulerScheduler(RulerScheduler rulerScheduler) {
        this.rulerScheduler = rulerScheduler;
    }

    public Timing getTiming() {
        return timing;
    }

    public void setTiming(Timing timing) {
        this.timing = timing;
    }

    //https://www.hl7.org/fhir/valueset-task-status.html
    public void setStatus(String statusString) {

        this.task.setStatus(TaskStatus.fromCode(statusString));
    }

    public void setStatus(TaskStatus status) {

        this.task.setStatus(status);
    }


    public TaskStatus getStatus() {

        return this.task.getStatus();
    }

    // cap_small :routine, urgent, asap, stat,
    public void setPriority(String priority) {

        this.task.setPriority(TaskPriority.fromCode(priority));
    }

    public void setPriority(TaskPriority priority) {

        this.task.setPriority(priority);
    }

    public TaskPriority getPriority() {

        return task.getPriority();
    }

    // unknown,  proposal, plan, order, originalorder, reflexorder, fillerorder, instanceorder, option,
    public void setIntent(String intent) {

        this.task.setIntent(TaskIntent.fromCode(intent));
    }

    public void setIntent(TaskIntent intent) {

        this.task.setIntent(intent);
    }

    public TaskIntent getIntent() {

        return task.getIntent();
    }

    @Override
    public abstract void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException;
}

