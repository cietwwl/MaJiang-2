package com.zxz.domain;

import java.util.Date;

/**��Ϣ
 * @author Administrator
 *
 */
public class Message {
    private Integer id;

    private Integer type;//���� 0 ���� ��Ϣ

    private String message;//��Ϣ

    private Date createDate; 

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}