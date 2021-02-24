#ifndef MARKDOWN_H
#define MARKDOWN_H

#include"parse.h"
#include<vector>
class markdown {
private:
    vector<string>infile; //读取文件内容
    //vector<int> skip_line;
    string content;        //输出文件内容
    string mdName;         //读取文件名称
public:
    //friend void lineTrans(vector<string>&line,vector<int> &skip_line);
    markdown(string _filename) {
        mdName = _filename;
    }
    ~markdown();
    void getfile(); //读文件，将其内容写入容器
    void process();  //转换处理
    void toHTML();     //将content内容写成html文件
};
#endif // MARKDOWN_H
