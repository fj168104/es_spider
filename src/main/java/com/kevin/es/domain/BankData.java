package com.kevin.es.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 爬取数据实体类
 *
 * Created by bysocket on 21/07/2017.
 */

@Data
@ToString
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

    /**
     * 格式化信息
     */
    //当事人
    private String partyPerson;

    //金融机构名称
    private String bankName;

    //法人名称
    private String holderName;

    //主要违法违规事实（案由）
    private String mainCase;

    //行政处罚依据
    private String according;

    //行政处罚决定
    private String decision;

    //作出处罚决定的机关名称
    private String orgName;




//    @Override
//    public String toString() {
//        return "BankData{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", OriUrl=" + OriUrl +
//                ", score=" + score +
//                ", issueDate=" + issueDate +
//                '}';
//    }
}
