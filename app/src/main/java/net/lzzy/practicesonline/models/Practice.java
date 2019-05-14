package net.lzzy.practicesonline.models;

import net.lzzy.practicesonline.constants.ApiConstants;
import net.lzzy.sqllib.Ignored;
import net.lzzy.sqllib.Jsonable;
import net.lzzy.sqllib.Sqlitable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by lzzy_gxy on 2019/4/16.
 * Description:
 */
public class Practice extends BaseEntity implements Sqlitable, Jsonable {

    @Ignored
    static final String COL_NAME="name";
    @Ignored
    static final String COL_OUTLINES="outlines";
    @Ignored
    static final String COL_DOWNLOAD_DATE="downloadDate";
    @Ignored
    static final String COL_API_ID="apiId";



    private String name;
    private int questionCount;
    private Date downloadDate;
    private String outlines;
    private boolean isDownload;
    private int apiId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }

    public String getOutlines() {
        return outlines;
    }

    public void setOutlines(String outlines) {
        this.outlines = outlines;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    @Override
    public boolean needUpdate() {
        return false;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return null;
    }

    @Override
    public void fromJson(JSONObject jsonObject) throws JSONException {
        apiId=jsonObject.getInt(ApiConstants.JSON_PRACTICE_API_ID);
        name=jsonObject.getString(ApiConstants.JSON_PRACTICE_NAME);
        outlines=jsonObject.getString(ApiConstants.JSON_PRACTICE_OUTLINES);
        questionCount=jsonObject.getInt(ApiConstants.JSON_PRACTICE_QUESTION_COUNT);
        downloadDate=new Date();
    }
}
