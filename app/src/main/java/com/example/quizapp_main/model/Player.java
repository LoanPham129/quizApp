package com.example.quizapp_main.model;

public class Player {
    private String name;
    private int questionNumber; // Số câu trả lời đúng
    private int money; // Số tiền

    // Constructor mặc định cho Firebase
    public Player() {
    }

    public Player(String name, int questionNumber, int money) {
        this.name = name;
        this.questionNumber = questionNumber;
        this.money = money;
    }

    // Getter và Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
