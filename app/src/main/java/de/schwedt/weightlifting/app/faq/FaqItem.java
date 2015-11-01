package de.schwedt.weightlifting.app.faq;

public class FaqItem {

    private String header = "";
    private String question = "";
    private String answer = "";

    public FaqItem(String header, String question, String answer) {
        this.header = header;
        this.question = question;
        this.answer = answer;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
