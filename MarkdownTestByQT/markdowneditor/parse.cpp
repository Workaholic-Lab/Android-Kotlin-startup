#include"parse.h"

bool if_para=false;
bool if_ulist=false;
bool if_olist=false;
bool if_quote=false;
bool if_ulist2=false;
bool if_olist2=false;
bool if_table=false;
bool if_table1=false;
extern vector<int>skip_line;
//常用量

//斜体
regex italic_regex("\\*(.*?)\\*");

//粗体
regex bold_regex("\\*\\*(.*?)\\*\\*");

//粗斜
regex bolditalic_regex("\\*\\*\\*(.*?)\\*\\*\\*");

//无序列表
regex uList1_regex("^\\*\\s(.*)");
regex uList2_regex("^\\+\\s(.*)");
regex uList3_regex("^\\-\\s(.*)");

regex uList12_regex("^\\t\\*\\s(.*)");
regex uList22_regex("^\\t\\+\\s(.*)");
regex uList32_regex("^\\t\\-\\s(.*)");
//有序列表
regex oList_regex("^[0-9]*\\.\\s(.*)");
regex oList2_regex("^\\t[0-9]*\\.\\s(.*)");

//标题
regex h1_regex("^#\\s(.*)");
regex h2_regex("^##\\s(.*)");
regex h3_regex("^###\\s(.*)");
regex h4_regex("^####\\s(.*)");
regex h5_regex("^#####\\s(.*)");
regex h6_regex("^######\\s(.*)");

//引用
regex quote_regex("^>(.*)");

//分割线
regex splitline1_regex("-{3,}");
regex splitline2_regex("\\*{3,}");

//超链接
regex hyperlink_regex(("\\[(.*)\\]\\((.*)\\)"));

//表格
regex table_regex("^\\|(.*)\\|$");
regex tbleft_regex("\\s*:\\s*-+\\s*");
regex tbcenter_regex("\\s*:-+:\\s*");
regex tbright_regex("\\s*-+\\s*:\\s*");

void strTrans(string& resultString, string& search, string& replace) {
    for (size_t pos = 0; ; pos += replace.length())
    {
        pos = resultString.find(search, pos);
        if (pos == string::npos)
            break;

        resultString.erase(pos, search.length());
        resultString.insert(pos, replace);
    }
}
void lineTrans(vector<string>&line) {
    int tbNum = 1;
    int tbNum_ = 1;
    int tbstyle[10];
    memset(tbstyle, 0, sizeof(int) * 10);
    for (int lineIndex = 0;lineIndex < line.size();lineIndex++) {
        if(lineIndex==0){
            if_para=false;
            if_ulist=false;
            if_olist=false;
            if_quote=false;
            if_ulist2=false;
            if_olist2=false;
            if_table=false;
            if_table1=false;
        }
        string tempString = line[lineIndex];
        string replace;
        string search;
        smatch match;
        if (tempString.empty()) {
            //if_para = false;
            if_quote = false;
            if_ulist = false;
            if_olist = false;
            if_ulist2 = false;
            if_olist2 = false;
            tempString += "<p></p>";
            line[lineIndex] = tempString;
            continue;
        }

        replace = "";
        search = "";
        if (regex_match(tempString, match, table_regex)&&if_table==true) {
            if (if_table1 == false) {
                search = "</tbody></table>";
                strTrans(line[lineIndex - 2], search, replace);
                tempString.replace(tempString.begin(), tempString.begin() + 1, "<tr>");
                tempString.replace(tempString.end() - 1, tempString.end(), "</td></tr></tbody></table>");
                switch (tbstyle[0]) {
                case 1:
                    tempString.insert(4, "<td style=\"text-align:left\">");
                    break;
                case 2:
                    tempString.insert(4, "<td style=\"text-align:center\">");
                    break;
                case 3:
                    tempString.insert(4, "<td style=\"text-align:right\">");
                    break;
                }
                for (int k = 4, m = 1;k < tempString.length();k++) {
                    if (tempString[k] == '|') {
                        tempString.erase(k, 1);
                        switch (tbstyle[m]) {
                        case 1:
                            tempString.insert(k, "</td><td style=\"text-align:left\">");
                            break;
                        case 2:
                            tempString.insert(k, "</td><td style=\"text-align:center\">");
                            break;
                        case 3:
                            tempString.insert(k, "</td><td style=\"text-align:right\">");
                            break;
                        }
                        m++;
                    }
                }
                if_table1 = true;
            }
            else {
                search = "</tbody></table>";
                strTrans(line[lineIndex - 1], search, replace);
                tempString.replace(tempString.begin(), tempString.begin() + 1, "<tr>");
                tempString.replace(tempString.end() - 1, tempString.end(), "</td></tr></tbody></table>");
                switch (tbstyle[0]) {
                case 1:
                    tempString.insert(4, "<td style=\"text-align:left\">");
                    break;
                case 2:
                    tempString.insert(4, "<td style=\"text-align:center\">");
                    break;
                case 3:
                    tempString.insert(4, "<td style=\"text-align:right\">");
                    break;
                }
                for (int k = 4, m = 1;k < tempString.length();k++) {
                    if (tempString[k] == '|') {
                        tempString.erase(k, 1);
                        switch (tbstyle[m]) {
                        case 1:
                            tempString.insert(k, "</td><td style=\"text-align:left\">");
                            break;
                        case 2:
                            tempString.insert(k, "</td><td style=\"text-align:center\">");
                            break;
                        case 3:
                            tempString.insert(k, "</td><td style=\"text-align:right\">");
                            break;
                        }
                        m++;
                    }
                }
            }
        }
        else {
            if_table = false;
            if_table1 = false;
            tbNum = 1;
            tbNum_ = 1;
            memset(tbstyle, 0, sizeof(int) * 10);
        }
        while (regex_search(tempString, match, bolditalic_regex)) {
            replace += "<strong><em>";
            replace += match[1];
            replace += "</em></strong>";
            search += "***";
            search += match[1];
            search += "***";
            strTrans(tempString, search, replace);
        }
        replace = "";
        search = "";
        while (regex_search(tempString, match, bold_regex)) {
            replace += "<strong>";
            replace += match[1];
            replace += "</strong>";
            search += "**";
            search += match[1];
            search += "**";
            strTrans(tempString, search, replace);
        }
        replace = "";
        search = "";
        while (regex_search(tempString, match,italic_regex)) {
            replace += "<em>";
            replace += match[1];
            replace += "</em>";
            search += "*";
            search += match[1];
            search += "*";
            strTrans(tempString, search, replace);
        }
        replace = "";
        search = "";
        if (regex_match(tempString,match,splitline1_regex)) {
            tempString="<hr>";
        }
        if (regex_match(tempString, splitline2_regex)) {
            tempString= "<hr>";
        }

        replace = "";
        search = "</blockquote>";
        if (if_quote == true&&!regex_match(tempString,match, uList1_regex)&&!regex_match(tempString, match, uList2_regex)&& !regex_match(tempString, match, uList3_regex) && !regex_match(tempString, match, oList_regex)) {

            strTrans(line[lineIndex - 1], search, replace);
            tempString += "</blockquote>";
        }
        if (regex_match(tempString,match,quote_regex)&&if_quote==false) {
            replace += "<blockquote>";
            replace += match[1];
            replace += "</blockquote>";
            tempString = replace;
            if_quote = true;
            if_ulist = false;
            if_olist = false;
        }

        replace = "";
        search = "</ul>";
        if (if_ulist == true && !regex_match(tempString, match, quote_regex) && !regex_match(tempString, match, oList_regex)) {
            if (if_ulist2 == true&&!regex_match(tempString, match, oList2_regex)) {
                search = "</ul></li></ul>";
                strTrans(line[lineIndex - 1], search, replace);
                replace = "<li>";
                if (regex_match(tempString, match, uList12_regex) || regex_match(tempString, match, uList22_regex) || regex_match(tempString, match, uList32_regex)) {
                    replace += match[1];
                }
                else replace += tempString;
                replace += "</li></ul></li></ul>";
                tempString = replace;
            }
            else if (if_olist2 == true&& !(regex_match(tempString, match, uList12_regex) || regex_match(tempString, match, uList22_regex) || regex_match(tempString, match, uList32_regex))) {
                search = "</ol></li></ul>";
                strTrans(line[lineIndex - 1], search, replace);
                replace = "<li>";
                if (regex_match(tempString, match, oList2_regex)) {
                    replace += match[1];
                }
                else replace += tempString;
                replace += "</li></ol></li></ul>";
                tempString = replace;
            }
            else if ((regex_match(tempString, match, uList12_regex) || regex_match(tempString, match, uList22_regex) || regex_match(tempString, match, uList32_regex))&&if_ulist2==false) {
                search = "</li></ul>";
                strTrans(line[lineIndex - 1], search, replace);
                replace = "<ul><li>";
                replace += match[1];
                replace += "</li></ul></li></ul>";
                tempString = replace;
                if_ulist2 =true;
                if_olist2 = false;
            }
            else if (regex_match(tempString, match, oList2_regex) && if_olist2 == false) {
                search = "</li></ul>";
                strTrans(line[lineIndex - 1], search, replace);
                replace = "<ol><li>";
                replace += match[1];
                replace += "</li></ol></li></ul>";
                tempString = replace;
                if_ulist2 = false;
                if_olist2 = true;
            }
            else {
                strTrans(line[lineIndex - 1], search, replace);
                replace = "";
                replace += "<li>";
                if (regex_match(tempString, match, uList1_regex) || regex_match(tempString, match, uList2_regex) || regex_match(tempString, match, uList3_regex)) {
                    replace += match[1];
                }
                else replace += tempString;
                replace += "</li></ul>";
                tempString = replace;
            }
        }
        replace = "";
        if ((regex_match(tempString, match, uList1_regex)|| regex_match(tempString, match, uList2_regex)|| regex_match(tempString, match, uList3_regex)) && if_ulist == false) {
            replace += "<ul><li>";
            replace += match[1];
            replace += "</li></ul>";
            tempString = replace;
            if_ulist = true;
            if_quote = false;
            if_olist = false;
            if_ulist2 = false;
            if_olist2 = false;
        }






        replace = "";
        search = "</ol>";
        if (if_olist == true && !regex_match(tempString, match, quote_regex) && !regex_match(tempString, match, uList1_regex) && !regex_match(tempString, match, uList2_regex) && !regex_match(tempString, match, uList3_regex)) {
            if (if_ulist2 == true && !regex_match(tempString, match, oList2_regex)) {
                search = "</ul></li></ol>";
                strTrans(line[lineIndex - 1], search, replace);
                replace = "<li>";
                if (regex_match(tempString, match, uList12_regex) || regex_match(tempString, match, uList22_regex) || regex_match(tempString, match, uList32_regex)) {
                    replace += match[1];
                }
                else replace += tempString;
                replace += "</li></ul></li></ol>";
                tempString = replace;
            }
            else if (if_olist2 == true && !(regex_match(tempString, match, uList12_regex) || regex_match(tempString, match, uList22_regex) || regex_match(tempString, match, uList32_regex))) {
                search = "</ol></li></ol>";
                strTrans(line[lineIndex - 1], search, replace);
                replace = "<li>";
                if (regex_match(tempString, match, oList2_regex)) {
                replace += match[1];
                }
                else replace += tempString;
                replace += "</li></ol></li></ol>";
                tempString = replace;
            }
            else if (regex_match(tempString, match, oList2_regex)&&if_olist2==false) {
                search = "</li></ol>";
                strTrans(line[lineIndex - 1], search, replace);
                replace = "<ol><li>";
                replace += match[1];
                replace += "</li></ol></li></ol>";
                tempString = replace;
                if_ulist2 == false;
                if_olist2 == true;
            }
            else if (regex_match(tempString, match, uList12_regex) || regex_match(tempString, match, uList22_regex) || regex_match(tempString, match, uList32_regex)&&if_ulist==false) {
                search = "</li></ol>";
                strTrans(line[lineIndex - 1], search, replace);
                replace = "<ul><li>";
                replace += match[1];
                replace += "</li></ul></li></ol>";
                tempString = replace;
                if_ulist2 = true;
                if_olist2= false;
            }
            else {
                strTrans(line[lineIndex - 1], search, replace);
                replace = "";
                replace += "<li>";
                if (regex_match(tempString, match, oList_regex)) {
                    replace += match[1];
                }
                else replace += tempString;
                replace += "</li></ol>";
                tempString = replace;
            }
        }
        if (regex_match(tempString, match, oList_regex) && if_olist == false) {
            replace += "<ol><li>";
            replace += match[1];
            replace += "</li></ol>";
            tempString = replace;
            if_olist = true;
            if_quote = false;
            if_ulist = false;
            if_ulist2 = false;
            if_olist2 = false;
        }

        replace = "";
        search = "";
        while (regex_search(tempString, match, hyperlink_regex)) {
            replace += "<a href=\"";
            replace += match[2];
            replace += "\">";
            replace += match[1];
            replace += "</a></p>";
            search += "[";
            search += match[1];
            search += "](";
            search += match[2];
            search += ")";
            strTrans(tempString, search, replace);
        }


        if (regex_search(tempString, match, h6_regex)) {
            replace += "<h6>";
            replace += match[1];
            replace += "</h6>";
            tempString = replace;
        }
        if (regex_search(tempString, match, h5_regex)) {
            replace += "<h5>";
            replace += match[1];
            replace += "</h5>";
            tempString = replace;
        }
        if (regex_search(tempString, match, h4_regex)) {
            replace += "<h4>";
            replace += match[1];
            replace += "</h4>";
            tempString = replace;
        }
        if (regex_search(tempString, match, h3_regex)) {
            replace += "<h3>";
            replace += match[1];
            replace += "</h3>";
            tempString = replace;
        }
        if (regex_search(tempString, match, h2_regex)) {
            replace += "<h2>";
            replace += match[1];
            replace += "</h2>";
            tempString = replace;
        }
        if (regex_search(tempString, match, h1_regex)) {
            replace += "<h1>";
            replace += match[1];
            replace += "</h1>";
            tempString = replace;
        }
        if (regex_match(tempString, match, table_regex)) {
            string tempString2 =line[lineIndex + 1];
            if (regex_match(tempString2, match, table_regex)) {
                for (int i = 1;i < tempString.length() - 1;i++) {
                    if (tempString[i] == '|') tbNum++;
                }
                int j = 0;
                for (int i = 1;i < tempString2.length();i++) {
                    if (tempString2[i] == '|') {
                        string temp = tempString2.substr(j+1, i-j-1);
                        if (regex_match(temp, match, tbleft_regex) ) {
                            tbstyle[tbNum_ - 1] = 1;
                            tbNum_++;
                            j = i;
                        }
                        else if (regex_match(temp, match, tbcenter_regex)) {
                            tbstyle[tbNum_ - 1] = 2;
                            tbNum_++;
                            j = i;
                        }
                        else if (regex_match(temp, match, tbright_regex)) {
                            tbstyle[tbNum_ - 1] = 3;
                            tbNum_++;
                            j = i;
                        }
                        else {
                            if_table = true;
                            break;
                        }

                    }
                }
                if (tbNum != tbNum_-1||if_table==true) {
                    tbNum = 1;
                    tbNum = 1;
                    if_table = false;
                    memset(tbstyle, 0, sizeof(int) * 10);
                }
                else {
                    if_table = true;
                    tempString.replace(tempString.begin(), tempString.begin() + 1, "<table><thead><tr>");
                    tempString.replace(tempString.end() - 1, tempString.end(), "</th></tr></thead><tbody></tbody></table>");
                    switch (tbstyle[0]) {
                    case 1:
                        tempString.insert(18, "<th style=\"text-align:left\">");
                        break;
                    case 2:
                        tempString.insert(18, "<th style=\"text-align:center\">");
                        break;
                    case 3:
                        tempString.insert(18, "<th style=\"text-align:right\">");
                        break;
                    }
                    for (int k = 18, m = 1;k < tempString.length();k++) {
                        if (tempString[k] == '|') {
                            tempString.erase(k, 1);
                            switch (tbstyle[m]) {
                            case 1:
                                tempString.insert(k, "</th><th style=\"text-align:left\">");
                                break;
                            case 2:
                                tempString.insert(k, "</th><th style=\"text-align:center\">");
                                break;
                            case 3:
                                tempString.insert(k, "</th><th style=\"text-align:right\">");
                                break;
                            }
                            m++;
                        }
                    }
                }
            }
        }
        line[lineIndex] = tempString;
        if (if_table == true && if_table1 == false) {
            lineIndex++;
            skip_line.push_back(lineIndex);
        }
    }
}
