package net.lzzy.practicesonline.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import net.lzzy.practicesonline.models.view.QuestionResult;
import net.lzzy.practicesonline.models.view.WrongType;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.practicesonline.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author lzzy_gxy
 * @date 2019/4/17
 * Description:
 */
public class UserCookies {
    private static final String KEY_TIME = "keyTime";
    private static final int FLAG_COMMIT_N = 0;
    private static final int FLAG_COMMIT_Y = 1;
    public static final String Id_SPLITTER = ",";
    /**
     * 记录时间
     */
    private SharedPreferences spTime;
    private SharedPreferences spCommit;
    private SharedPreferences spPosition;
    private SharedPreferences spReadCount;
    private SharedPreferences spOption;

    private static final UserCookies INSTANCE = new UserCookies();

    private UserCookies() {
        spTime = AppUtils.getContext().getSharedPreferences("refresh_time", Context.MODE_PRIVATE);
        spCommit = AppUtils.getContext().getSharedPreferences("practice_commit", Context.MODE_PRIVATE);
        spPosition = AppUtils.getContext().getSharedPreferences("question_position", Context.MODE_PRIVATE);
        spReadCount = AppUtils.getContext().getSharedPreferences("read_count", Context.MODE_PRIVATE);
        spOption = AppUtils.getContext().getSharedPreferences("option_radio", Context.MODE_PRIVATE);
    }

    public static UserCookies getInstance() {
        return INSTANCE;
    }

    public void updateLastRefreshTime() {
        String time = DateTimeUtils.DATE_TIME_FORMOT.format(new Date());
        spTime.edit().putString(KEY_TIME, time).apply();
    }

    public String getLastRefreshTime() {
        return spTime.getString(KEY_TIME, "");
    }

    public boolean isPracticeCommitted(String practice) {
        int result = spCommit.getInt(practice, FLAG_COMMIT_N);
        return result == FLAG_COMMIT_Y;
    }

    public void commitPractice(String practiceId) {
        spCommit.edit().putInt(practiceId,FLAG_COMMIT_Y).apply();
    }

    public void updateCurrentQuestion(String practiceId, int pos) {
        spPosition.edit().putInt(practiceId, pos).apply();
    }

    public int getCurrentQuestion(String practiceId) {
        return spPosition.getInt(practiceId, 0);
    }

    public int getReadCount(String questionId) {
        return spReadCount.getInt(questionId, 0);
    }

    public void updateReadCount(String questionId) {
        int count = getReadCount(questionId) + 1;
        spReadCount.edit().putInt(questionId, count).apply();
    }

    public void changeOptionState(Option option, boolean isChecked, boolean isMulti) {
        String ids = spOption.getString(option.getQuestionId().toString(), "");
        String id = option.getId().toString();
        if (isMulti) {
            if (isChecked && !ids.contains(id)) {
                ids = ids.concat(Id_SPLITTER).concat(id);
            } else if (! isChecked && ids.contains(id)) {
                ids = ids.replace(id, "");
            }
        } else {
            if (isChecked) {
                ids = id;
            }
        }
        spOption.edit().putString(option.getQuestionId().toString(), trunkSplitter(ids)).apply();
    }
    public boolean isOptionSelected(Option option) {
        String ids = spOption.getString(option.getQuestionId().toString(), "");
        return Objects.requireNonNull(ids).contains(option.getId().toString());
    }
    private String trunkSplitter(String ids) {
        boolean isSplitterRepeat = true;
        String repeatSolitter = Id_SPLITTER.concat(Id_SPLITTER);
        while (isSplitterRepeat) {
            isSplitterRepeat = false;
            if (ids.contains(repeatSolitter)) {
                isSplitterRepeat = true;
                ids = ids.replace(repeatSolitter, Id_SPLITTER);
            }
        }
        if (ids.endsWith(Id_SPLITTER)) {
            ids = ids.substring(0, ids.length() - 1);
        }
        if (ids.startsWith(Id_SPLITTER)) {
            ids = ids.substring(1);
        }
        return ids;
    }



    public List<QuestionResult> getResultFromCookies(List<Question> questions){
        List<QuestionResult> results=new ArrayList<>();
        for (Question question:questions){
            QuestionResult result=new QuestionResult();
            result.setQuestionId(question.getId());
            String checkedIds=spOption.getString(question.getId().toString(),"");
            result.setRight(isUserRight(checkedIds,question).first);
            result.setType(isUserRight(checkedIds,question).second);
            results.add(result);
        }
        return results;
    }

    private Pair<Boolean, WrongType> isUserRight(String checkIds, Question question){
        boolean miss=false,extra=false;
        for (Option option:question.getOptions()){
            if (option.isAnswer()){
                if (!checkIds.contains(option.getId().toString())){
                    miss=true;
                }
            }else {
                if (checkIds.contains(option.getId().toString())){
                    extra=true;
                }
            }
        }
        if (miss&&extra){
            return new Pair<>(false,WrongType.WRONG_OPTIONS);
        }else if (miss){
            return new Pair<>(false,WrongType.MISS_OPTIONS);
        }else if (extra){
            return new Pair<>(false,WrongType.EXTRA_OPTIONS);
        }else {
            return new Pair<>(true,WrongType.RIGHT_OPTIONS);
        }
    }

}
