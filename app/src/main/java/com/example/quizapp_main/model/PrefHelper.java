package com.example.quizapp_main.model;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefHelper {
    private static final String PREF_NAME = "user_pref";

    // Keys cho dữ liệu người dùng
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_MONEY = "money";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // Keys cho dữ liệu trò chơi
    private static final String KEY_TOTAL_EARNED = "total_earned";

    // Phương thức lưu thông tin người dùng hoàn chỉnh
    public static void saveUserData(Context context, String userId, String username, int money) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putInt(KEY_MONEY, money);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // Lấy thông tin người dùng
    public static String getUserId(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_USER_ID, null);
    }

    public static String getUsername(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_USERNAME, null);
    }

    public static int getMoney(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_MONEY, 0);
    }
    // Cập nhật số tiền
    public static void updateMoney(Context context, int amount) {
        int current = getMoney(context);
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_MONEY, current + amount)
                .putInt(KEY_TOTAL_EARNED, getTotalEarned(context) + amount)
                .apply();
    }
    // Quản lý trạng thái đăng nhập
    public static boolean isLoggedIn(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_IS_LOGGED_IN, false);
    }
    public static int getTotalEarned(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_TOTAL_EARNED, 0);
    }
    public static void logout(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_IS_LOGGED_IN)
                .apply();
    }
    public static void updateQuestionNumber(Context context, int questionNumber) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt("question_number", questionNumber) // Lưu số câu hỏi đã trả lời
                .apply();
    }

    // Lấy số câu hỏi đã trả lời từ SharedPreferences
    public static int getQuestionNumber(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt("question_number", 0); // Mặc định là 0 nếu chưa lưu
    }
}