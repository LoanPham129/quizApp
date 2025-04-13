package com.example.quizapp_main.model;

public class Player {
    private String name, id;
    private int questionNumber;
    private int money;

    // Constructor mặc định (Firebase cần cái này để mapping dữ liệu)
    public Player() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
