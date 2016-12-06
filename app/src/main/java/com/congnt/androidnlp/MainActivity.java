package com.congnt.androidnlp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button btn_open, btn_reset, btn_check_search;
    private ImageButton btn_search;
    private EditText et_search;
    private TextView result, list_result;
    public int errorWordCount = 0;
    public int errorWordCountByDatabase = 0;
    public int wordCount = 0;
    private ArrayList<String> listFiles;
    public List<Rule> rules = new ArrayList<>();
    public String dbString;
    public String test = "";
    private int errorWordCountByNguyenAmDatabase;
    private List<String> dbAmTiet;
    private SwitchCompat switchCompat;
    private Set<String> listPath = new HashSet<>();
    private Map<String, String> mapPath = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn_open = (Button) findViewById(R.id.btn_open);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_check_search = (Button) findViewById(R.id.btn_check_search);
        btn_search = (ImageButton) findViewById(R.id.btn_search);
        result = (TextView) findViewById(R.id.result);
        et_search = (EditText) findViewById(R.id.et_search);
        list_result = (TextView) findViewById(R.id.list);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilePickerBuilder.getInstance().setMaxCount(50)
                        .setSelectedFiles(null)
                        .setActivityTheme(R.style.AppTheme)
                        .pickDocument(MainActivity.this);

            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = et_search.getText().toString().replace("\\s+", " ").trim();
                //TODO: Search
                for (Map.Entry<String, String> entry : mapPath.entrySet()) {
                    if (entry.getValue().contains(input)) {
                        list_result.append(entry.getKey() + ": \t" + input + "\n");
                    }else{
                        String[] arrays = input.split(" ");
                        if (arrays.length > 1) {
                            input="";
                            for (int i = 0; i < arrays.length-1; i++) {
                                input+=arrays[i]+" ";
                            }
                            if (entry.getValue().contains(input.trim())) {
                                list_result.append(entry.getKey() + ": \t" + input + "\n");
                            }
                        }
                    }
                }
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.setText("");
                list_result.setText("");
                errorWordCount = 0;
                errorWordCountByDatabase = 0;
                wordCount = 0;
            }
        });
        btn_check_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = et_search.getText().toString().replace("\\s+", " ").trim();
                Toast.makeText(MainActivity.this, input, Toast.LENGTH_SHORT).show();
                list_result.append(processLine(input));
            }
        });
//        dbString = FileUtil.loadFileFromRaw(this, R.raw.db);
/*

        dbString = getVietnamese(dbString);
        //Split db
        Set<String> set = new HashSet<>();
        String dbs[] = dbString.split(" ");
        for (String db : dbs) {
            String tmp="";
            for (int i = 0; i < db.length(); i++) {
                char a=db.charAt(i);
                if (!phuam.contains(""+a)){
                    if (i==1 && a=='i' && db.charAt(0)=='g'){
                        continue;
                    }else {
                        tmp = db.substring(i, db.length());
                        set.add(tmp);
                    }
                    break;
                }
            }
        }

        for (String s : set) {
            test+=" "+s;
        }
//        Log.d(TAG, "onCreate: "+test);

        int maxLogSize = 1000;
        for(int i = 0; i <= test.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > test.length() ? test.length() : end;
            Log.v(TAG, test.substring(start, end));
        }
*/


        dbAmTiet = Arrays.asList(FileUtil.loadFileFromRaw(this, R.raw.db3).split(" "));
        //init rule
        rules.add(new Rule01());
        rules.add(new Rule02());
        rules.add(new Rule03());
        rules.add(new Rule04());
        rules.add(new Rule05());
        rules.add(new Rule06());
        rules.add(new Rule07());
        rules.add(new Rule08());
        rules.add(new Rule09());
        rules.add(new Rule10());
        rules.add(new Rule11());
        rules.add(new Rule12());
        rules.add(new Rule13());
        rules.add(new Rule14());
        rules.add(new Rule15());
        rules.add(new Rule16());
        rules.add(new Rule17());
        rules.add(new Rule18());
        rules.add(new Rule19());
        rules.add(new Rule20());
        rules.add(new Rule21());
        rules.add(new Rule22());
        rules.add(new Rule23());
        rules.add(new Rule24());
//        rules.add(new Rule25());
        rules.add(new Rule29());
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
//                ArrayList<String> docPaths = new ArrayList<>();
                listPath = new HashSet<>();
                listPath.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                for (String docPath : listPath) {
                    new ReadFileAsync(docPath).execute();
                }
            }
        }
    }

    public class ReadFileAsync extends AsyncTask<Void, String, Void> {
        public String path;

        public ReadFileAsync(String path) {
            this.path = path;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                InputStream inputStream = new FileInputStream(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    publishProgress(processLine(line));
                }
                mapPath.put(path, stringBuilder.toString());
                inputStream.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            list_result.append(values[0]);
        }

        @Override
        protected void onPostExecute(Void v) {
            result.setText("Error Count: " + errorWordCount + "/" + wordCount
                    + " - " + errorWordCountByNguyenAmDatabase);
        }
    }

    public String phuam = "qrtpsdghklxcvbnmđ";
    public String notinVietNam = "!@#$%^&*()_-+]\"\\:;\\/?><.`,wfjz0123456789";

    private String processLine(String line) {
        StringBuilder sb = new StringBuilder();
        line = getVietnamese(line);
        String[] token = line.toLowerCase().trim().split(" ");
        for (String s : token) {
            wordCount++;
            s = s.replaceAll("\uFEFF", "");
            if (s.length() >= 1) {
//                if (s.length() == 1 && (daucau.contains(s) || !phuam.contains(s))) {
//                } else {
//                    while (s.length()>0 && daucau.contains(s.charAt(s.length() - 1) + "")){
//                        s = s.substring(0, s.length() - 1);
//                    }
//                    while (s.length()>0 && daucau.contains(s.charAt(0) + "")){
//                        s = s.substring(0, s.length() - 1);
//                    }
                if (isInvalid(s)) {
                    sb.append("Rule: " + r + ":\t\t" + s + "\n");
                    errorWordCount++;
                }
//                }
            }
        }
        return sb.toString();
    }

    //    public String daucau = ".,\\:;-?!()*\"";
    public int r;

    private boolean isInvalid(String s) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).checkInvalid(s)) {
                r = i + 1;
                return true;
            }
        }
        return false;
    }


    public String getVietnamese(String x) {
        String out = "";
        String charNotInTV = "wzjf";
        for (int i = 0; i < x.length(); i++) {
            if ((Character.isLetter(x.charAt(i)) && !charNotInTV.contains("" + x.charAt(i))) || Character.isDigit(x.charAt(i))) {
                out += "" + x.charAt(i);
            } else {
                out += " ";
            }
        }
        return out.replaceAll("\\s+", " ").trim();
    }


    //    RULE
    public abstract class Rule {
        public String phuam = "qrtpsdghklxcvbnmđ";
        public String phuamcuoi = "tpghcnm";
        public String phuam_kocophuamtheosau = "qrsdhlxvbmđ";
        public String nguyenam_iey = "ieêy í ị ĩ ì ỉ é è ẽ ẻ ẹ ế ể ề ễ ệ ý ỳ ỷ ỹ ỵ";
        public String nguyenam_ie = "ieê í ị ĩ ì ỉ é è ẽ ẻ ẹ ế ể ề ễ ệ";
        public String nguyenam_sacnang = "á ạ ắ ặ ấ ậ é ẹ ế ệ í ị ý ỵ ó ọ ố ộ ớ ợ ú ụ ứ ự";
        public String notinVietNam = "!@#$%^&*()_-+]\"\\:;\\/?><.`,wfjz0123456789";
        String amdem = "oa óa òa ọa ỏa õa oă oắ oằ oặ oẳ oe òe óe ọe ỏe õe uê uế uề uể uệ uễ";
        String amdem2 = "oa óa òa ọa ỏa õa oă oắ oằ oặ oẳ oe òe óe ọe ỏe õe uê uế uề uể uệ uễ uâ uấ uậ uẩ uẫ uy ụy úy ùy ủy ũy";

//        public String hoinga = "ả ẳ ẩ ẻ ể ỷ ủ ử ỉ ỏ ổ ở ã ẵ ẫ ẽ ễ ỹ ũ ữ ĩ õ ỗ ỡ";
//        public String nanghuyen = "ạ ặ ậ ẹ ệ ỵ ụ ự ị ọ ộ ợ à ằ ầ è ề ỳ ù ừ ì ò ồ ờ";
//        public String nga = "ã ẵ ẫ ẽ ễ ỹ ũ ữ ĩ õ ỗ ỡ";
//        public String notinVietNam = "!@#$%^&*()_-+]\"\\:;\\/?><.`,wfjz0123456789";

        public abstract boolean checkInvalid(String x);
    }

    /**
     * trả về true nếu xâu x không phải là xâu tiếng việt:
     * - 1 xâu không phải là xâu tiếng việt nếu có chuwasc scas ký tự không nằm trong tiếng việt bao gồm
     * 0-9 các ký tự trong bảng mã ASCII, các ký tự chỉ xuất hiện trong tiếng anh
     */
    public class Rule01 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            for (int i = 0; i < x.length(); i++) {
                if (notinVietNam.contains("" + x.charAt(i))) {
                    return true;
                }
            }
            return false;
        }

    }

    /**
     * Tieng viet sai chính tả nếu không có nguyên âm nào trong từ đó.
     */
    public class Rule02 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            int count = 0;
            for (int i = 0; i < x.length(); i++) {
                if (phuam.contains("" + x.charAt(i))) {
                    count++;
                }
            }
            if (count == x.length()) {
                return true;
            }
            return false;
        }
    }

    /**
     * Từ tiếng việt bị giới hạn về số lượng chữ cái ở bên trong. Tối đa là 7
     */
    public class Rule03 extends Rule {
        int MAX = 7;

        @Override
        public boolean checkInvalid(String x) {
            if (x.trim().length() > MAX) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Từ tiếng việt có tối đa 3 nguyên âm
     */
    public class Rule04 extends Rule {
        int maxnguyenam = 3;

        @Override
        public boolean checkInvalid(String x) {
            int countnguyenam = 0;
            for (int i = 0; i < x.length(); i++) {
                if (!phuam.contains("" + x.charAt(i))) {
                    countnguyenam++;
                }
            }
            if (countnguyenam > maxnguyenam) {
                return true;
            }
            return false;
        }
    }

    /**
     * Từ tiếng việt nếu có >1 nguyên âm thì các nguyên âm này phải nằm cạnh nhau
     */
    class Rule05 extends Rule {
        @Override
        public boolean checkInvalid(String x) {

            int count = 0;
            for (int i = 0; i < x.length(); i++) {
                if (phuam.contains(x.charAt(i) + "") == false) {
                    count++;
                }
            }
            // tìm vị trí nguyên âm đầu tiên
            int vitri = 0;
            if (count > 1) {
                for (int i = 0; i < x.length(); i++) {
                    if (phuam.contains(x.charAt(i) + "") == false) {// neu la nguyen am
                        vitri = i;
                        break;
                    }
                }

                for (int j = vitri + 1; j < vitri + count; j++) {
                    if (phuam.contains(x.charAt(j) + "")) {// neu phu am
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * không phải phụ âm nào cũng đứng cuối của 1 từ
     */
    class Rule06 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            for (int i = 0; i < x.length(); i++) {
                char a = x.charAt(x.length() - 1);
                if (phuam.contains(a + "") && !phuamcuoi.contains(a + "")) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * trả về true nếu có 2 nguyên âm giống hệt nhau đứng cạnh nhau - oo
     */
    class Rule07 extends Rule {
        //neuu
        @Override
        public boolean checkInvalid(String x) {
            for (int i = 0; i < x.length(); i++) {
                if (!phuam.contains("" + x.charAt(i))) {
                    if (i < (x.length() - 1)) {
                        if (x.charAt(i) == x.charAt(i + 1) && x.charAt(i) != 'o' && x.charAt(i + 1) != 'o') {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    /**
     * trả về true nếu từ x có chữa 1 phụ âm p1 đi theo nó là phụ âm p2 nhưng trong tiếng việt
     * k được phép có bất kỳ phụ âm nào đi sau
     */
    class Rule08 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            for (int i = 0; i < x.length() - 1; i++) {
                if (phuam_kocophuamtheosau.contains("" + x.charAt(i))) {
                    if (phuam.contains("" + x.charAt(i + 1)))
                        return true;
                }
            }
            return false;
        }
    }

    /**
     * Nếu 1 từ có phụ âm n và sau n lại là 1 phụ âm thì phụ âm sau n phải là g hoặc h
     * Ví dụ : nhanh, nghiêng, nhà
     * Neu 1 tu co phu am la c hoac g va sau c hoac gla 1 phu am thi phu am do phai la h
     */
    class Rule09 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            char a;
            for (int i = 0; i < x.length() - 1; i++) {
                if (x.charAt(i) == 'n') {
                    a = x.charAt(i + 1);
                    if (phuam.contains(a + "")) { // neu la phụ âm
                        if (a != 'h' && a != 'g')
                            return true;

                    }
                }
                if (x.charAt(i) == 'c' || x.charAt(i) == 'g') {
                    a = x.charAt(i + 1);
                    if (phuam.contains(a + "")) { // neu la phụ âm
                        if (a != 'h')
                            return true;

                    }
                }
            }
            return false;
        }
    }

    /**
     * nếu 1 từ bắt đầu bằng phụ âm q, thì sau đó phải là nguyên âm u
     */
    class Rule10 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            if (x.charAt(0) == 'q') {
                if (x.charAt(1) != 'u') {
                    return true;
                }

            }
            return false;
        }
    }

    /**
     * Nếu phụ âm đầu là k đằng sau là 1 nguyên âm thì thì nguyên âm đi theo nó phải là e , i ê,y
     */
    class Rule11 extends Rule {
        @Override
        public boolean checkInvalid(String x) {

            if (x.charAt(0) == 'k') {
                if (phuam.contains(x.charAt(1) + "") == false) {
                    if (nguyenam_iey.contains(x.charAt(1) + "") == false)
                        return true;
                }
            }
            return false;
        }
    }

    /**
     * Nếu phụ âm đầu là c thì nguyên âm đi theo nó không phải phải là e , i ê
     */
    class Rule12 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            if (x.charAt(0) == 'c') {
                if (nguyenam_ie.contains(x.charAt(1) + ""))
                    return true;
            }
            return false;
        }
    }

    /**
     * nếu xâu bắt đầu bằng phụ âm gh hoặc ngh thì nguyên âm đi kèm phải là i, e, ê
     */
    class Rule13 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            if (x.length() >= 3 && x.charAt(0) == 'g' && x.charAt(1) == 'h') {
                if (nguyenam_ie.contains(x.charAt(2) + "") == false) {
                    return true;
                }

            }
            if (x.length() >= 4 && x.charAt(0) == 'n' && x.charAt(1) == 'g' && x.charAt(2) == 'h') {
                if (nguyenam_ie.contains(x.charAt(3) + "") == false) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * nếu xâu bắt đầu bởi ng hoặc g và sau đó là 1 nguyên âm thì nguyên âm đó không phải là e, i, ê
     */
    class Rule14 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
        /*
        nếu chiều dài xâu >=2 và bắt đầu bằng g và sau g là 1 nguyên âm trừ gì
         */
            String gi = "gìgỉgĩgịgigí";
            if (x.length() >= 2) {
                String x1 = x.substring(0, 2);
                if (gi.contains(x1)) return false;
            }
            if (x.length() >= 2 && x.charAt(0) == 'g'
                    && phuam.contains(x.charAt(1) + "") == false) {
                if (nguyenam_ie.contains(x.charAt(1) + "")) {
                    return true;
                }
            }
            if (x.length() >= 3 && x.charAt(0) == 'n' && x.charAt(1) == 'g'
                    && phuam.contains(x.charAt(2) + "") == false) {
                if (nguyenam_ie.contains(x.charAt(2) + "")) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 1 từ không bao gio có 3 phụ âm giống nhau
     */
    class Rule15 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            for (int i = 0; i < x.length(); i++) {
                char a = x.charAt(i);
                if (phuam.contains("" + a)) {
                    int count = 1;
                    for (int j = i + 1; j < x.length(); j++) {
                        if (x.charAt(j) == a) {
                            count++;
                        }
                    }
                    if (count >= 3) return true;
                }
            }
            return false;
        }
    }

    /**
     * Nếu bắt đầu bằng phụ âm p,k sau đó là 1 phụ âm thì phụ âm đó phải là h
     */
    class Rule16 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            char a = x.charAt(0);
            if (a == 'p' || a == 'k') {
                if (phuam.contains(x.charAt(1) + "")) {
                    if (x.charAt(1) != 'h')
                        return true;
                }
            }
            return false;
        }
    }

    /**
     * Nếu 1 từ bắt đầu bằng phụ âm t , sau đó là 1 phụ âm thì phải là h haoc r
     * Check t cuoi cau thi truoc no la nguyen am co dau sac hoac nang
     */
    class Rule17 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            if (x.charAt(0) == 't') {
                char a = x.charAt(1);
                if (phuam.contains(a + "")) {
                    if (a != 'h' && a != 'r')
                        return true;
                }
            }
            if (x.charAt(x.length() - 1) == 't') {
                char a = x.charAt(x.length() - 2);
                if (!nguyenam_sacnang.contains(a + "")) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Neeus phụ âm th và ph,gh,kh,th, tr không bao giờ đứng cuối 1 từ vi du phaph,
     */
    class Rule18 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            String reverse = new StringBuffer(x).
                    reverse().toString();// dảo ngược xâu hpahp
            if (reverse.charAt(0) == 'h') {
                if (reverse.charAt(1) == 'p' || reverse.charAt(1) == 'g'
                        || reverse.charAt(1) == 'k' || reverse.charAt(1) == 't') {
                    return true;
                }
            }
            if (reverse.charAt(0) == 'r') {
                if (reverse.charAt(1) == 't')
                    return true;
            }
            return false;
        }
    }

    /**
     * 1 từ có tối đa là 5 phụ âm: nghiêng ,
     */
    class Rule19 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            int count = 0;
            for (int i = 0; i < x.length(); i++) {
                if (phuam.contains(x.charAt(i) + "")) {
                    count++;
                }
            }
            if (count > 5) return true;
            return false;
        }
    }

    /**
     * Check P
     */
    class Rule20 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            if (x.charAt(0) == 'p') {
                if (x.charAt(1) != 'h') {
                    return true;
                }
            }
            if (x.charAt(x.length() - 1) == 'p') {
                if (!nguyenam_sacnang.contains(x.charAt(x.length() - 2) + "")) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Check H
     */
    class Rule21 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            String beforeH = "kcngtp";
            if (x.charAt(x.length() - 1) == 'h') {
                if (x.charAt(x.length() - 2) != 'n' && x.charAt(x.length() - 2) != 'c') {
                    return true;
                }
            }
            if (x.charAt(0) == 'h') {
                if (phuam.contains("" + x.charAt(1))) {
                    return true;
                }
            }
            for (int i = 1; i < x.length(); i++) {
                if (x.charAt(i) == 'h') {
                    if (!beforeH.contains("" + x.charAt(i - 1))) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * Check S dau cau: sau S ko duoc la oa, oă, oe, uê
     */
    class Rule22 extends Rule {
        List<String> notAfterS = Arrays.asList(amdem.split(" "));

        @Override
        public boolean checkInvalid(String x) {
            if (x.length() > 2 && x.charAt(0) == 's') {
                String a = x.substring(1, 3);
                if (notAfterS.contains(a)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Check N dau cau: sau N ko duoc la am dem cua oa, oă, oe, uê uâ uy
     */
    class Rule23 extends Rule {
        public List<String> notAfterN = Arrays.asList(amdem2.split(" "));

        @Override
        public boolean checkInvalid(String x) {
            if (x.length() > 2 && x.charAt(0) == 'n') {
                String a = x.substring(1, 3);
                if (notAfterN.contains(a)) {
                    return true;
                }
            }
            return false;
        }
    }
//
//    /**
//     * Check CH dau cau: sau CH khong the di voi dau nang va huyen
//     */
//    class Rule24 extends Rule {
//        @Override
//        public boolean checkInvalid(String x) {
//            if (x.length() > 2 && x.charAt(0) == 'c' && x.charAt(1) == 'h') {
//                for (int i = 2; i < x.length(); i++) {
//                    if (nanghuyen.contains("" + x.charAt(i))) {
//                        return true;
//                    }
//                }
//            }
//            return false;
//        }
//    }

    /**
     * R và GI không kết hợp với âm đệm
     */
    class Rule24 extends Rule {
        public List<String> listAmdem = Arrays.asList(amdem2.split(" "));

        @Override
        public boolean checkInvalid(String x) {
            if (x.length() > 2 && x.charAt(0) == 'r') {
                String a = x.substring(1, 3);
                if (listAmdem.contains(a)) {
                    return true;
                }
            }
            if (x.length() > 3 && x.charAt(0) == 'g' && x.charAt(1) == 'i') {
                String a = x.substring(2, 4);
                if (listAmdem.contains(a)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Check Database NGuyen am: Neu khong co cac nguyen am nay thi tra ve true
     */
    class Rule29 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            String tmp = "";
            for (int i = 0; i < x.length(); i++) {
                char a = x.charAt(i);
                if (!phuam.contains("" + a)) {
                    if (i == 1 && a == 'i' && x.charAt(0) == 'g') {
                        continue;
                    } else {
                        tmp = x.substring(i, x.length());
                    }

                    break;
                }
            }

            if (!dbAmTiet.contains(tmp)) {
                errorWordCountByNguyenAmDatabase++;
                return true;
            }
            return false;
        }
    }

    /**
     * Check Database
     */
    class Rule30 extends Rule {
        @Override
        public boolean checkInvalid(String x) {
            if (!dbString.contains(x)) {
                errorWordCountByDatabase++;
                return true;
            }
            return false;
        }
    }
}
