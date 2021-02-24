#include"parse.h";

extern bool if_para;
extern bool if_ulist;
extern bool if_olist;
extern bool if_quote;
extern bool if_ulist2;
extern bool if_olist2;
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
	for (int lineIndex = 0;lineIndex < line.size();lineIndex++) {
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
		line[lineIndex] = tempString;
	}
}
