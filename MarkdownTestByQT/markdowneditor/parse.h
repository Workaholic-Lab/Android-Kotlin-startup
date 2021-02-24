#ifndef PARSE_H
#define PARSE_H
#include<iostream>
#include<string>
#include<fstream>
#include <regex>
using namespace std;
//行处理
void lineTrans(vector<string>&line);
//行处理字符替换
void strTrans(string& resultString, string& search, string& replace);
#endif // PARSE_H
