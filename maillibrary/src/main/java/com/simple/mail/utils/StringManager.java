package com.simple.mail.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.CharacterStyle;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.simple.base.base.BaseApplication;
import com.simple.base.constant.Constant;
import com.simple.base.utils.FileUtils;
import com.simple.mail.entity.Attach;
import com.simple.mail.entity.Image;
import com.simple.mail.entity.Person;
import com.simple.mail.maillibrary.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 */
public class StringManager {

    private static final String comma = "、";

    /**
     * 验证邮箱地址是否正确
     */
    public static boolean checkEmail(String email, boolean isNeedUseRegex) {
        if (isNeedUseRegex) {
            boolean flag = false;
            try {
                //            String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
                String check = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
                Pattern regex = Pattern.compile(check);
                Matcher matcher = regex.matcher(email);
                flag = matcher.matches();
            } catch (Exception e) {
                flag = false;
            }
            return flag;
        } else {
            if (!TextUtils.isEmpty(email) && email.contains("@") && !email.startsWith("@") & !email.endsWith("@")) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean isEmail(String string) {
        if (string == null)
            return false;
//        String regex = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
//        Pattern p;
//        Matcher m;
//        p = Pattern.compile(regex);
//        m = p.matcher(string);
//        if (m.matches())
//            return true;
//        else
//            return false;
        return checkEmail(string, false);
    }

    /**
     * 验证邮箱地址是否正确
     */
    public static boolean checkEmail(TextView tv) {

        return checkEmail(getStringByTv(tv), false);
    }


    /**
     * 验证手机号码
     */
    public static boolean isMobileNO(String mobiles) {
        boolean flag = false;
        if (!TextUtils.isEmpty(mobiles)) {//^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|7[0678])\\d{8}$
            try {
                Pattern p = Pattern
                        .compile("0?(13|14|15|16|17|18|19)[0-9]{9}");
                Matcher m = p.matcher(mobiles);
                flag = m.matches();
            } catch (Exception e) {
                flag = false;
            }
        }
//        if (!TextUtils.isEmpty(mobiles)&&mobiles.startsWith("1")&&mobiles.length()==11){
//            flag = true;
//        }
        return flag;
    }


    /**
     * 将TextView中中文本获取到
     */
    public static String getStringByTv(TextView tv) {
        return tv.getText().toString().trim();
    }

    /**
     * 判断textview是否没有文字
     */
    public static boolean isEmpty(TextView textView) {
        return TextUtils.isEmpty(textView.getText().toString().trim());
    }


    /**
     * 获取字符串资源
     */
    public static String getString(Context context, int id) {
        return context.getResources().getString(id);
    }

    /**
     * 判断用户输入姓名信息是否合格
     */
    public static boolean checkNameMessage(String userName) {
        // 用户名只能是中文或者英文
        if (isChineseChar(userName) || isEnglishChar(userName)) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否为数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断用户名是否为中文
     */
    public static String getCountStr(int count) {
        String string = "";
        if (count != 0) {
            string = String.valueOf(count);
        }
        return string;
    }

    /**
     * 判断用户名是否为中文
     */
    public static boolean isChineseChar(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断用户名是否为英文
     */
    public static boolean isEnglishChar(String str) {
        Pattern p = Pattern.compile("^[a-zA-Z]+$");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为数字，英文，汉语中的一种
     */
    public static boolean isStringOk(String str) {
        return (isEnglishChar(str) || isChineseChar(str) || isNumeric(str)) && !StringManager.isContainEmoji(str);
    }


    /**
     * 去掉html中的标签，提取文字
     */
    public static String removeHtmlTag(String inputString) {
        if (inputString == null)
            return null;
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        Pattern p_script;
        Matcher m_script;
        Pattern p_style;
        Matcher m_style;
        Pattern p_html;
        Matcher m_html;
        Pattern p_special;
        Matcher m_special;
        try {
            //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
            //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
            // 定义HTML标签的正则表达式
            String regEx_html = "<[^>]+>";
            // 定义一些特殊字符的正则表达式 如：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            String regEx_special = "\\&[a-zA-Z]{1,10};";
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签
            p_special = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);
            m_special = p_special.matcher(htmlStr);
            htmlStr = m_special.replaceAll(""); // 过滤特殊标签
            textStr = htmlStr.replaceAll("</?[^>]+>|\r|\n|", "").trim();
            textStr = textStr.length() <= 100 ? textStr : textStr.substring(0, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textStr;// 返回文本字符串
    }

    /**
     * 发件人前面的小圆圈里的字
     */
    public static String getCtv(String from) {
        String ctv = "";
        if (!TextUtils.isEmpty(from)) {
            ctv = extractionChinese(from);
            if (!TextUtils.isEmpty(ctv)) {
                ctv = ctv.substring(ctv.length() - 1, ctv.length());
            } else {
                ctv = from.substring(0, 1);
            }
            if (StringManager.isEnglishChar(ctv)) {
                ctv = ctv.toUpperCase();
            }
        }
        return ctv;
    }

    /**
     * 提取字符串中的汉字
     */
    public static String extractionChinese(String str) {
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");
        StringBuilder sb = new StringBuilder();
        Matcher m = p.matcher(str);
        while (m.find())
            for (int i = 0; i <= m.groupCount(); i++) {
                sb.append(m.group());
            }
        return sb.toString();
    }

    /**
     * 转换发件人,收件人，抄送人，密送人
     */
    public static Person conversionToContacts(String string, String myMailAccount) {
        if (!TextUtils.isEmpty(string)) {
            String mail = string.substring(string.indexOf(Constant.separator1) + 1, string.length());
            String name = string.substring(0, string.indexOf(Constant.separator1));
            Person person = new Person(name, mail);
            if (person.email.equals(myMailAccount)) {
                person.name = "我";
            }
            return person;
        } else {
            return null;
        }
    }

    /**
     * 转换发件人,收件人，抄送人，密送人
     */
    public static ArrayList<Person> conversionToContactses(String string, String myMailAccount) {
        ArrayList<Person> list = new ArrayList<>();
        if (!TextUtils.isEmpty(string)) {
            String[] split = string.split(Constant.separator2);
            for (String s : split) {
                list.add(conversionToContacts(s, myMailAccount));
            }
        }
        return list;
    }

    /**
     * 转换发件人,收件人，抄送人，密送人
     */
    public static String conversionToContactsString(ArrayList<Person> list) {
        if (list.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                if (!isEmail(list.get(i).email)) {
                    throw new IllegalArgumentException("包含不正确的邮件地址: " + (list.get(i).email));
                }
                sb.append(list.get(i).name + Constant.separator1 + list.get(i).email + Constant.separator2);
            }
            String string = sb.toString();
            return string.substring(0, string.length() - 1);
        }
        return "";
    }

    /**
     * 邮件相同认为是同一个
     */
    public static boolean containsContacts(Person person, List<Person> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).email.equals(person.email) && list.get(i).name.equals(person.name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转换附件
     * 将附件的信息转化成字符串，“路径,uid,isload;”这个顺序保存
     */
    public static String conversionToAttachsString(List<Attach> attachs) {
        if (attachs.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < attachs.size(); i++) {
                Attach attach = attachs.get(i);
                sb.append(attach.file_path + ";");
            }
            String string = sb.toString();
            return string.substring(0, string.length() - 1);
        }
        return "";
    }

    /**
     * 转换附件
     */
    public static ArrayList<Attach> conversionToAttachs(String string) {
        ArrayList<Attach> list = new ArrayList<>();
        if (!TextUtils.isEmpty(string)) {
            String[] split = string.split(";");
            for (int i = 0; i < split.length; i++) {
                File file = new File(split[i]);
                list.add(new Attach(file.getName(), file.getAbsolutePath(), Formatter.formatFileSize(BaseApplication.getApplication(), file.length())));
            }
        }
        return list;
    }

    /**
     * 如果tv的结尾没有“、”则给他加上“、”
     */
    public static void addComma(TextView tv) {
        if (tv == null) return;

        String string = getStringByTv(tv);
        string = string.endsWith(comma) ? string : string + comma;
        tv.setText(string);
    }

    /**
     * 如果tv的结尾有“、”则去掉“、”
     */
    public static void removeComma(TextView tv) {
        if (tv == null) return;
        ;
        String string = getStringByTv(tv);
        string = string.endsWith(comma) ? string.substring(0, string.length() - 1) : string;
        tv.setText(string);
    }

    /**
     * 如果tv的结尾有“、”则去掉“、”
     */
    public static String removeComma(String string) {
        return string.endsWith(comma) ? string.substring(0, string.length() - 1) : string;
    }


    /**
     * spanned 转化成HTML
     */
    public static String convertSpannedToRichText(Spanned spanned, boolean isWithImage) {
        List<CharacterStyle> spanList = Arrays.asList(spanned.getSpans(0, spanned.length(), CharacterStyle.class));
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(spanned);
        for (CharacterStyle characterStyle : spanList) {
            int start = stringBuilder.getSpanStart(characterStyle);
            int end = stringBuilder.getSpanEnd(characterStyle);
            if (start >= 0) {
                String htmlStyle = handleCharacterStyle(characterStyle, isWithImage);

                if (htmlStyle != null) {
                    stringBuilder.replace(start, end, htmlStyle);
                }
            }
        }
        return stringBuilder.toString().replaceAll("\n", "<br/>").replaceAll(" ", "&nbsp;").replaceAll("✺", " ");
    }

    private static String handleCharacterStyle(CharacterStyle characterStyle, boolean isWithImage) {
        if (characterStyle instanceof ImageSpan) {
            ImageSpan span = (ImageSpan) characterStyle;
            if (isWithImage) {//带本地路径
                return String.format("<img✺src=\"%s\">", "file://" + span.getSource());
            } else {//带cid
                return String.format("<img✺src=\"cid:%s\">", FileUtils.getMd5(new File(span.getSource())));
            }
        }
        return null;
    }

    /**
     * 图片路径将替换成cid
     * 回复邮件的时候，把无照片的内容转换成有图片的内容，并把图片保存到images中
     */
    public static String withImage2NoImage(String html, List<Image> imageList) {
        List<Image> images = new ArrayList<>();
        String label = "img";
        String tagAttrib = "src";
        String regxpForLabel = "<\\s*" + label + "\\s+([^>]*)\\s*";
        String regxpForTagAttrib = tagAttrib + "=\\s*\"([^\"]+)\"";

        Pattern patternForLabel = Pattern.compile(regxpForLabel, Pattern.CASE_INSENSITIVE);
        Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib, Pattern.CASE_INSENSITIVE);

        Matcher matcherForLabel = patternForLabel.matcher(html);

        StringBuffer sb = new StringBuffer();
        boolean result = matcherForLabel.find();

        int indexOfTag = 0;
        while (result) {

            StringBuffer sbreplace = new StringBuffer("<" + label + " ");
            Matcher matcherForAttrib = patternForAttrib.matcher(matcherForLabel.group(1));
            if (matcherForAttrib.find()) {
                String attributeStr = matcherForAttrib.group(1);

                if (indexOfTag < imageList.size()) {
                    Image image = imageList.get(indexOfTag);
                    if (attributeStr.contains(image.path)) {
                        matcherForAttrib.appendReplacement(sbreplace, "src=\"cid:" + image.cid + "\"");
                        images.add(image);
                    }
                }
            }

            matcherForAttrib.appendTail(sbreplace);
            matcherForLabel.appendReplacement(sb, sbreplace.toString());
            result = matcherForLabel.find();
            indexOfTag++;
        }
        matcherForLabel.appendTail(sb);
        imageList.clear();
        imageList.addAll(images);
        return sb.toString();
    }


    /**
     * 读取html中所有img标签的src值
     */
    public static List<String> getImgSrc(String htmlStr) {
        if (TextUtils.isEmpty(htmlStr) || !htmlStr.contains("<img")) {
            return new ArrayList<>();
        }
        String img = "";
        Pattern p_image;
        Matcher m_image;
        List<String> pics = new ArrayList<String>();
//       String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址
        String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            img = img + "," + m_image.group();
            // Matcher m =
            // Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(img); //匹配src
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                String replace = m.group(1).trim().replace("file://", "");
                pics.add(replace);
            }
        }
        return pics;
    }


    public static boolean isContainEmoji(String string) {
        Pattern p = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(string);
        return m.find();
    }


    public static String formatFileSize(int size) {
        return Formatter.formatFileSize(BaseApplication.getApplication(), size);
    }
}
