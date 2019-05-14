package net.lzzy.practicesonline.models;

import net.lzzy.practicesonline.constants.DbConstants;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/5/8.
 * Description:
 */
public class OptionFactory {
    private static final OptionFactory OUR_INSTANCE=new OptionFactory();
    private SqlRepository<Option> repository;
    private SqlRepository<Question> questionSqlRepository;

    public OptionFactory(){
        repository=new SqlRepository<>(AppUtils.getContext(),Option.class, DbConstants.packager);
    }

    public static OptionFactory getInstance(){
        return OUR_INSTANCE;
    }
    //public QuestionFactory(){
    //            repository=new SqlRepository<>(AppUtils.getContext(),Question.class, DbConstants.packager);
    //            optionRepository=new SqlRepository<>(AppUtils.getContext(),Option.class,DbConstants.packager);
    //    }
    public Option getByOptionText(String text,String questionId){
        try {
            Question question=QuestionFactory.getInstance().getById(questionId);
            List<Option> options=question.getOptions();
            List<Option> checkOption=repository.getByKeyword(text,new String[]{Option.COL_CONTENT},true);
            Option result=checkOption.get(0);
            for (int i=0;i<checkOption.size();i++){
                if (options.contains(checkOption.get(i))){
                    result=checkOption.get(i);
//                    return checkOption.get(i);
                }
            }

            return result;
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return new Option();
        }
    }

}
