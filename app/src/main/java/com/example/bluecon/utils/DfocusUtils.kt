package com.example.bluecon.utils

import android.content.Context
import com.google.gson.Gson

/**
 * 2024/08/22 - jhkang
 * DfocusUtils - Preference
 */
class DfocusUtils {
    companion object {

        /**
         * Boolean을 저장하는 함수
         *
         * @param context : 앱 컨텍스트
         * @param pref_name : SharedPreference 이름
         * @param key : 값을 저장하는 키값
         * @param value : 저장할 Boolean 값
         * @return Boolean true or false
         */
        fun saveBoolean(context: Context, pref_name: String, key: String, value: Boolean): Boolean {
            return try {
                with(context.getSharedPreferences(pref_name, Context.MODE_PRIVATE).edit()) {
                    putBoolean(key, value)
                    apply()
                }

                true

            } catch (ex: Exception) {
                false
            }
        }

        /**
         * Boolean을 읽어오는 함수
         *
         * @param context : 앱 컨텍스트
         * @param pref_name : SharedPreference 이름
         * @param key : 값을 저장하는 키값
         * @return Boolean 저장된 값
         */
        fun readBoolean(context: Context, pref_name: String, key: String): Boolean {
            return try {
                val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)

                pref.getBoolean(key, false)


            } catch (ex: java.lang.Exception) {
                false
            }
        }

        /**
         * Int를 저장하는 함수
         *
         * @param context : 앱 컨텍스트
         * @param pref_name : SharedPreference 이름
         * @param key : 값을 저장하는 키값
         * @param value : 저장할 Int 값
         * @return Boolean true or false
         */
        fun saveInt(context: Context, pref_name: String, key: String, value: Int): Boolean {
            return try {
                with(context.getSharedPreferences(pref_name, Context.MODE_PRIVATE).edit()) {
                    putInt(key, value)
                    apply()
                }

                true

            } catch (ex: Exception) {
                false
            }
        }

        /**
         * Int를 읽어오는 함수
         *
         * @param context : 앱 컨텍스트
         * @param pref_name : SharedPreference 이름
         * @param key : 값을 저장하는 키값
         * @return Int 저장된 값
         */
        fun readInt(context: Context, pref_name: String, key: String): Int {
            return try {
                val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)

                pref.getInt(key, 0)
            } catch (ex: java.lang.Exception) {
                -1
            }
        }

        /**
         * Long을 저장하는 함수
         *
         * @param context : 앱 컨텍스트
         * @param pref_name : SharedPreference 이름
         * @param key : 값을 저장하는 키값
         * @param value : 저장할 Long 값
         * @return Boolean true or false
         */
        fun saveLong(context: Context, pref_name: String, key: String, value: Long): Boolean {
            return try {
                with(context.getSharedPreferences(pref_name, Context.MODE_PRIVATE).edit()) {
                    putLong(key, value)
                    apply()
                }

                true

            } catch (ex: Exception) {
                false
            }
        }

        /**
         * Long을 읽어오는 함수
         *
         * @param context : 앱 컨텍스트
         * @param pref_name : SharedPreference 이름
         * @param key : 값을 저장하는 키값
         * @return Long 저장된 값
         */
        fun readLong(context: Context, pref_name: String, key: String): Long {
            return try {
                val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)

                pref.getLong(key, 0)
            } catch (ex: java.lang.Exception) {
                -1
            }
        }

        /**
         * Float를 저장하는 함수
         *
         * @param context : 앱 컨텍스트
         * @param pref_name : SharedPreference 이름
         * @param key : 값을 저장하는 키값
         * @param value : 저장할 Float 값
         * @return Boolean true or false
         */
        fun saveFloat(context: Context, pref_name: String, key: String, value: Float): Boolean {
            return try {
                with(context.getSharedPreferences(pref_name, Context.MODE_PRIVATE).edit()) {
                    putFloat(key, value)
                    apply()
                }

                true

            } catch (ex: Exception) {
                false
            }
        }

        /**
         * Float을 읽어오는 함수
         *
         * @param context : 앱 컨텍스트
         * @param pref_name : SharedPreference 이름
         * @param key : 값을 저장하는 키값
         * @return Float 저장된 값
         */
        fun readFloat(context: Context, pref_name: String, key: String): Float {
            return try {
                val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)

                pref.getFloat(key, 0.0F)
            } catch (ex: java.lang.Exception) {
                -1.0F
            }
        }

        /**
         * String을 저장하는 함수
         *
         * @param context : 앱 컨텍스트
         * @param pref_name : SharedPreference 이름
         * @param key : 값을 저장하는 키값
         * @param value : 저장할 String 값
         * @return Boolean true or false
         */
        fun saveString(context: Context, pref_name: String, key: String, value: String): Boolean {
            return try {
                with(context.getSharedPreferences(pref_name, Context.MODE_PRIVATE).edit()) {
                    putString(key, value)
                    apply()
                }

                true

            } catch (ex: Exception) {
                false
            }
        }

        /**
         * String을 읽어오는 함수
         *
         * @param context : 앱 컨텍스트
         * @param pref_name : SharedPreference 이름
         * @param key : 값을 저장하는 키값
         * @return String 저장된 값
         */
        fun readString(context: Context, pref_name: String, key: String): String? {
            return try {
                val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)

                pref.getString(key, "")
            } catch (ex: java.lang.Exception) {
                null
            }
        }

        /**
         * SharedPreference 값을 모두 삭제하는 함수
         *
         * @param context : 앱 컨텍스트
         * @param pref_name : SharedPreference 이름
         * @return Boolean true or false
         */
        fun clearAll(context: Context, pref_name: String): Boolean {
            return try {
                with(context.getSharedPreferences(pref_name, Context.MODE_PRIVATE).edit()) {
                    clear()
                    apply()
                }

                true

            } catch (ex: Exception) {
                false
            }
        }

        /**
         * JSON문자열이 정상적인 JSON 문자열인지 판별하는 함수
         *
         * @param json : 판별할 JSON 문자열
         * @return Boolean true or false
         */
        fun isValidJson(json: String?): Boolean {
            return try {
                Gson().fromJson(json, Any::class.java)
                true
            } catch (ex: Exception) {
                false
            }
        }
    }
}