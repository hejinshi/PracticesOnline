package net.lzzy.practicesonline.models;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import net.lzzy.practicesonline.constants.DbConstants;
import net.lzzy.practicesonline.models.view.QuestionType;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/4/17.
 * Description:
 */
public class QuestionFactory {
    //SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
    private static final QuestionFactory OUR_INSTANCE=new QuestionFactory();
    private SqlRepository<Question> repository;
    private SqlRepository<Option> optionRepository;

    public QuestionFactory(){
            repository=new SqlRepository<>(AppUtils.getContext(),Question.class, DbConstants.packager);
            optionRepository=new SqlRepository<>(AppUtils.getContext(),Option.class,DbConstants.packager);
    }

    public static QuestionFactory getInstance(){
        return OUR_INSTANCE;
    }

    private void completeQuestion(Question question) throws InstantiationException, IllegalAccessException {
        List<Option> options=optionRepository.getByKeyword(question.getId().toString(),
                new String[]{Option.COL_QUESTION_ID},true);
        question.setOptions(options);
        question.setDbType(question.getDbType());


    }

    public Question getById(String questionId){
        try{
            Question question=repository.getById(questionId);
            completeQuestion(question);
            return question;
        }catch (IllegalAccessException|InstantiationException e){
            e.printStackTrace();
            return null;
        }

    }

    public List<Question> getQuestionByPractice(String practiceId){
        try {
            List<Question> questions=repository.getByKeyword(practiceId,
                    new String[]{Question.COL_PRACTICE_ID},true);
            for (Question question:questions){
                completeQuestion(question);
            }
            return questions;
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();

    }
    public void insert(Question question){
        List<String> sqlActions=new ArrayList<>();
        for (Option option:question.getOptions()){
            sqlActions.add(optionRepository.getInsertString(option));
        }
        sqlActions.add(repository.getInsertString(question));
        repository.exeSqls(sqlActions);






       // optionRepository=repository.getInsertString(question);
    }

    public List<String> getDeleteString(Question question){
        List<String> sqlActions=new ArrayList<>();
        sqlActions.add(repository.getDeleteString(question));
        for (Option option:question.getOptions()){
            sqlActions.add(optionRepository.getDeleteString(option));
        }
        String f=FavoriteFactory.getInstance().getDeleteString(question.getId().toString());
        if (!TextUtils.isEmpty(f)){
            sqlActions.add(f);
        }
        return sqlActions;
        //todo:3
    }
}
