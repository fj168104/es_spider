package com.kevin.es.domain;

import java.io.Serializable;

/**
 * 爬取数据实体类
 *
 * Created by bysocket on 21/07/2017.
 */

public class BankData implements Serializable {

    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 原始链接
     */
    private String OriUrl;

    /**
     * 时间
     */
    private String issueDate;

    private String htmlStr;

    private float score;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriUrl() {
        return OriUrl;
    }

    public void setOriUrl(String oriUrl) {
        OriUrl = oriUrl;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getHtmlStr() {
        return htmlStr;
    }

    public void setHtmlStr(String htmlStr) {
        this.htmlStr = htmlStr;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "BankData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", OriUrl=" + OriUrl +
                ", score=" + score +
                ", issueDate=" + issueDate +
                '}';
    }
}
