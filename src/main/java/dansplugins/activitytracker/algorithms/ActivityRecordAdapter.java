package dansplugins.activitytracker.algorithms;

import dansplugins.activitytracker.objects.ActivityRecord;

/**
 * Adapter that makes ActivityRecord compatible with the generic TopRecordsAlgorithm.
 * 
 * @author Daniel McCoy Stephenson
 */
public class ActivityRecordAdapter implements TopRecordsAlgorithm.Scorable {
    private final ActivityRecord record;
    
    public ActivityRecordAdapter(ActivityRecord record) {
        this.record = record;
    }
    
    @Override
    public double getScore() {
        return record.getTotalHoursSpent();
    }
    
    public ActivityRecord getRecord() {
        return record;
    }
}