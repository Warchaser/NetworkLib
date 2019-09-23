package com.warchaser.networklib.download;

public class DownloadValueBean {

    private long progress;

    private long total;

    public DownloadValueBean(){

    }

    public DownloadValueBean(long progress, long total){
        setProgress(progress);
        setTotal(total);
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
