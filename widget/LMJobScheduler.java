@SuppressLint("NewApi")
public class LMJobScheduler {

    public static final int JOB_ID_DELAY = 0x10;
    public static final int JOB_ID_REPEAT = 0x11;
    public static final int JOB_ID_30_MINUTES = 0x12;

    /**
     * @param context
     */
    static void setUpdateJobDelay(Context context) {
        Logger.d("setUpdateJobDelay");
        JobInfo job = new JobInfo.Builder(
                JOB_ID_DELAY,
                new ComponentName(context, SyncJobService.class)
        )
                .setMinimumLatency(diffSaturday())
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler mJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (mJobScheduler != null) {
            mJobScheduler.schedule(job);
        }
    }

    /**
     * @param context
     */
    static void setUpdateJobRepeat(Context context) {
        Logger.d("setUpdateJobRepeat");

        JobInfo job = new JobInfo.Builder(
                JOB_ID_REPEAT,
                new ComponentName(context, SyncJobService.class)
        )
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(Const.TIME_WEEK)
                .build();

        JobScheduler mJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (mJobScheduler != null) {
            mJobScheduler.schedule(job);
        }
    }

}
