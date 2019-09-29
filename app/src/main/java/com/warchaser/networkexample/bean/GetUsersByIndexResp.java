package com.warchaser.networkexample.bean;

import java.util.List;

public class GetUsersByIndexResp {


    /**
     * pageNum : 0
     * pageSize : 10
     * isLast : true
     * array : [{"id":1,"name":"Warchaser","passwd":"123456","age":1000,"gender":0}]
     */

    private int pageNum;
    private int pageSize;
    private boolean isLast;
    private List<ArrayBean> array;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isIsLast() {
        return isLast;
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }

    public List<ArrayBean> getArray() {
        return array;
    }

    public void setArray(List<ArrayBean> array) {
        this.array = array;
    }

    public static class ArrayBean {
        /**
         * id : 1
         * name : Warchaser
         * passwd : 123456
         * age : 1000
         * gender : 0
         */

        private int id;
        private String name;
        private String passwd;
        private int age;
        private int gender;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }
    }
}
