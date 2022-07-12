package ar.edu.unicen.isistan.asistan.storage.database.reports;

import com.google.gson.annotations.JsonAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.UserState;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.UserManager;
import ar.edu.unicen.isistan.asistan.storage.preferences.user.User;

@JsonAdapter(ReportAdapter.class)
public class Report {

    public enum ReportType {
        USER_STATE(UserState.class);

        private Class<? extends Report> type;

        ReportType(Class<? extends Report> type) {
            this.type = type;
        }

        public Class<? extends Report> getReportTypeClass() {
            return this.type;
        }

        @Nullable
        public static ReportType getType(Class<? extends Report> reportClass) {
            for (ReportType type: ReportType.values()) {
                if (type.getReportTypeClass().equals(reportClass))
                    return type;
            }
            return null;
        }
    }

    private ReportType reportType;
    private String userId;
    private String installationId;
    private long reportTime;

    protected Report(@NotNull ReportType reportType) {
        this.reportType = reportType;
        this.reportTime = System.currentTimeMillis();
    }

    public void setUser(@NotNull User user) {
        this.userId = user.getId();
        this.installationId = user.getInstallationId();
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public long getReportTime() {
        return reportTime;
    }

    public void setReportTime(long reportTime) {
        this.reportTime = reportTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

}
