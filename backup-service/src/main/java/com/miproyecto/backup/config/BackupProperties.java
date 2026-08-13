package com.miproyecto.backup.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "backup")
public class BackupProperties {

    private String directory;
    private String logFile;
    private String cron = "0 0 2 * * *";
    private boolean enabled = true;
    private Retention retention = new Retention();

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getLogFile() {
        return logFile != null && !logFile.isBlank()
                ? logFile
                : (directory != null ? directory : ".") + "/backup-audit.log";
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Retention getRetention() {
        return retention;
    }

    public void setRetention(Retention retention) {
        this.retention = retention;
    }

    public static class Retention {

        private boolean enabled = true;
        private int daily = 7;
        private int weekly = 4;
        private int monthly = 12;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getDaily() {
            return daily;
        }

        public void setDaily(int daily) {
            this.daily = daily;
        }

        public int getWeekly() {
            return weekly;
        }

        public void setWeekly(int weekly) {
            this.weekly = weekly;
        }

        public int getMonthly() {
            return monthly;
        }

        public void setMonthly(int monthly) {
            this.monthly = monthly;
        }
    }
}