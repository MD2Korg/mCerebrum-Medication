package org.md2k.medication.ema;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import org.md2k.medication.Configuration;
import org.md2k.medication.MainActivity;
import org.md2k.medication.model.CategoryList;
import org.md2k.utilities.FileManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class EMA {
    public static final String MULTIPLE_CHOICE="multiple_choice";
    public static final String MULTIPLE_SELECT="multiple_select";
    public static final String TEXT="text";
    public static final String TEXT_NUMERIC ="text_numeric";
    CategoryList categoryList;
    ArrayList<Question> questions;
    public void createEMA(Context context) {
        questions = new ArrayList<>();
        FileManager.deleteFile(Configuration.EMA_MEDICATION_DIRECTORY+Configuration.EMA_MEDICATION_FILENAME);
        categoryList = Configuration.readSelectedMedicationList();
        if(categoryList==null){
            Toast.makeText(context,"!!! Error: Could not create questionnaire file.!!!",Toast.LENGTH_SHORT).show();
            return;
        }
        for(int i=0;i<categoryList.categories.length;i++){
            for(int j=0;j<categoryList.categories[i].medications.length;j++) {
                createQuestions(categoryList.categories[i].medications[j].generic_name);
            }

        }
        if(questions.size()==0){
            Toast.makeText(context,"!!! Error: Doesn't have anything to save.!!!",Toast.LENGTH_SHORT).show();
            return;

        }
        questions.add(createQuestionLast());
        try {
            FileManager.writeJSON(Configuration.EMA_MEDICATION_DIRECTORY, Configuration.EMA_MEDICATION_FILENAME, questions);
            Toast.makeText(context,"Success...questionnaire file creted.",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context,"!!! Error: Could not create questionnaire file.!!!",Toast.LENGTH_SHORT).show();
        }
    }
    void createQuestions(String name){
        int currentQNo=questions.size();
        questions.add(createQuestion1(name));
        questions.add(createQuestion2(name, currentQNo));
        questions.add(createQuestion3(name, currentQNo+1));
    }
    Question createQuestion1(String name){
        int q_no=questions.size();
        String q_text="Yesterday, did you take \""+name+"\" as prescribed?";
        String q_type=MULTIPLE_CHOICE;
        ArrayList<String> r_option=new ArrayList<>();
        r_option.add("Yes");
        r_option.add("No");
        return new Question(q_no, q_text, q_type, r_option,null);
    }
    Question createQuestion2(String name, int c_id){
        int q_no=questions.size();
        String q_text="CHECK ALL THAT APPLY FOR YESTERDAY ("+name+")";
        String q_type=MULTIPLE_SELECT;
        ArrayList<String> r_option=new ArrayList<>();
        r_option.add("I took my morning dosage");
        r_option.add("I took my afternoon dosage");
        r_option.add("I took my evening dosage");
        r_option.add("<UNSELECT_OTHER>I did NOT take any");
        r_option.add("I took more than my prescribed dosage");
        ArrayList<String> conditions=new ArrayList<>();
        conditions.add(Integer.toString(c_id)+":No");
        return new Question(q_no, q_text, q_type, r_option,conditions);
    }
    Question createQuestion3(String name, int c_id){
        int q_no=questions.size();
        String q_text="Why did you NOT take \""+name+"\" yesterday?\nPLEASE CHECK ONE";
        String q_type=MULTIPLE_CHOICE;
        ArrayList<String> r_option=new ArrayList<>();
        r_option.add("I did not feel well");
        r_option.add("Doctor's orders");
        r_option.add("I forgot");
        r_option.add("I did not have any more");
        r_option.add("I did not want to take it");
        ArrayList<String> conditions=new ArrayList<>();
        conditions.add(Integer.toString(c_id)+":I did NOT take any");
        return new Question(q_no, q_text, q_type, r_option,conditions);
    }
    Question createQuestionLast(){
        int q_no=questions.size();
        String q_text="Thank you for answering this Survey. Please click \"FINISH\".";
        return new Question(q_no, q_text, null, null,null);
    }

}
