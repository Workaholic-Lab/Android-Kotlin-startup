#include"markdown.h"
#include<fstream>

vector<int>skip_line;
void markdown::getfile() {
    ifstream fin(mdName);
    string tmp;
    if(!fin){
        cout<<"NOOOO!"<<endl;
    }
    if (fin) {
        while (getline(fin, tmp))
        {
            infile.push_back(tmp);
        }
    }
}
void markdown::process() {
    lineTrans(infile);
    for (int i = 0;i < infile.size();i++) {
        vector<int>::iterator  skipline = find(skip_line.begin(), skip_line.end(), i);
        if (skipline == skip_line.end()) {
            content += infile[i];
            content += '\n';
        }
    }

}
void markdown::toHTML() {
    ofstream fout("C:/Qt/Qt5.12.10/Examples/Qt-5.12.10/webenginewidgets/markdowneditor/result.html");
    string head = "<!DOCTYPE html><html><head>\
            <meta charset=\"utf-8\">\
            <title>Markdown</title>\
            <style>\
            body{font-family:\"Times New Roman\",Times,serif;}\
            h1 {\
                font-size: 48px;\
            }\
            h2 {\
                font-size: 36px;\
            }\
            h3 {\
                font-size: 28px;\
            }\
            h4 {\
                font-size: 24px;\
            }\
            h5 {\
                font-size: 21px;\
            }\
            h6 {\
                font-size: 18px;\
            }\
            blockquote {\
                border-left:.5em solid #eee;\
                padding: 0 2em;\
                margin-left:0;\
                max-width: 476px;\
            }\
            ul{list-style-type:square;}\
            ol{ list - style - type:upper - roman; }\
            a:link{color:##000000;text-decoration:none;}\
            a:visited{color: #00FF00;text-decoration:underline;background-color:#FFFF85;}\
            a:hover{color:#FF00FF;}\
            table,th,td{border:1px solid black;}\
            </style>\
            </head><body>";
    string end = "</body></html>";
    if (fout.is_open()) {
        fout << head << content << end;
        fout.close();
    }
}
            markdown::~markdown(){
    skip_line.clear();
}
