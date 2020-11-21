package com.example.light_it_up;

import java.util.Random;

public class GenerateCertNumber{
    private int certNumLength = 6;

    public String excuteGenerate() {
        Random random = new Random(System.currentTimeMillis());

        int range = (int)Math.pow(10,certNumLength);
        int trim = (int)Math.pow(10, certNumLength-1);
        int result = random.nextInt(range)+trim;

        if(result>range){
            result = result - trim;
        }

        return String.valueOf(result);
    }

    public int getCertNumLength() {
        return certNumLength;
    }

    public void setCertNumLength(int certNumLength) {
        this.certNumLength = certNumLength;
    }

    public static void main(String[] args) {
        GenerateCertNumber ge = new GenerateCertNumber();
        ge.setCertNumLength(5);
        System.out.println(ge.excuteGenerate());
    }
}