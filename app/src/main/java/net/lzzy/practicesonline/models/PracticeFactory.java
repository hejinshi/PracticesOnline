package net.lzzy.practicesonline.models;

import net.lzzy.practicesonline.constants.DbConstants;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/4/17.
 * Description:
 */
public class PracticeFactory {
    private static final PracticeFactory OUR_INSTANCE=new PracticeFactory();
    private SqlRepository<Practice> repository;

    public static PracticeFactory getInstance(){
        return OUR_INSTANCE;
    }

    private PracticeFactory(){
        repository=new SqlRepository<>(AppUtils.getContext(),Practice.class, DbConstants.packager);
    }

    public Practice getByApiId(int apiId){
        String a=String.valueOf(apiId);
        try {
            return repository.getByKeyword(a,new String[]{"apiId"},true).get(0);
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Practice> get(){
        return repository.get();
    }

    private Practice getById(String id){
        return repository.getById(id);
    }

    public List<Practice> searchPractices(String kw){
        try {
            return repository.getByKeyword(kw,new String[]{Practice.COL_NAME},false);
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    private boolean isPracticeInDb(Practice practice){
        try {
            return repository.getByKeyword(String.valueOf(practice.getApiId()),new String[]{Practice.COL_API_ID},true).size()>0;
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return true;
        }

    }

    public boolean add(Practice practice){
        if (isPracticeInDb(practice)){
            return false;
        }
        repository.insert(practice);
        return true;
    }

    public UUID getPracticeId(int apiId){
        try {
            List<Practice> practices=repository.getByKeyword(String.valueOf(apiId),new String[]{Practice.COL_API_ID},true);
            if (practices.size()>0){
                return practices.get(0).getId();
            }
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setPracticeDown(String id){
        Practice practice=getById(id);
        if (practice!=null){
            practice.setDownload(true);
            repository.update(practice);
        }

    }

    public void saveQuestions(List<Question> questions,UUID practiceId){
        for (Question question:questions){
            QuestionFactory.getInstance().insert(question);
        }
        setPracticeDown(practiceId.toString());
    }

    public boolean deletePracticeAndRelated(Practice practice){
        try {
            List<String> sqlAction = new ArrayList<>();
            sqlAction.add(repository.getDeleteString(practice));
            QuestionFactory factory = QuestionFactory.getInstance();
            List<Question> questions = factory.getQuestionByPractice(practice.getId().toString());
            if (questions.size() > 0) {
                for (Question q : questions) {
                    sqlAction.addAll(factory.getDeleteString(q));
                }
            }
            repository.exeSqls(sqlAction);
            if (!isPracticeInDb(practice)){
                //todo:清除Cookies
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }





}
