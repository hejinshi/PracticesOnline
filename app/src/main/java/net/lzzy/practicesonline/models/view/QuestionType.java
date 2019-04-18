package net.lzzy.practicesonline.models.view;

/**
 * Created by lzzy_gxy on 2019/4/16.
 * Description: viewModel
 */
public enum QuestionType {
    /**
     * viewModel
     * 题目类型
     */
    SINGLE_CHOICE("单项选择"),MULTI_CHOICE("不定项选择"),JUDGE("判断");
    private String name;
    QuestionType(String name){
        this.name=name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static QuestionType getInstance(int ordinal){
        for (QuestionType type:QuestionType.values()){
            if (type.ordinal()==ordinal){
                return type;
            }
        }
        return null;
    }

    /**
     *
     */
//    Type1(""),Type2(""),Type3("");
//
//    private final String name;
//
//    QuestionType(String s){
//        name=s;
//    }
//
//    @Override
//    public String toString() {
//        return super.toString();
//    }
//
//    public QuestionType getQuestionType(int i){
//        return null;
//    }




}
