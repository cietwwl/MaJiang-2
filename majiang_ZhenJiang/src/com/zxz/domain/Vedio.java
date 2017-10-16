package com.zxz.domain;

import java.util.Date;

/**
 * 录像功能
 */
public class Vedio {
	
    private Integer id;

    private Date createdate;//创建时间

    private String record;//记录信息

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record == null ? null : record.trim();
    }
}