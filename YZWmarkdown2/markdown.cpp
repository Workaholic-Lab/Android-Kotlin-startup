#include"markdown.h"
#include<fstream>
void markdown::getfile() {
	ifstream fin(mdName);
	string tmp;
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
		content += infile[i];
		content += '\n';
	}

}
void markdown::toHTML() {
	ofstream fout("result.html");
	string head = "<!DOCTYPE html><html><head>\
			<meta charset=\"utf-8\">\
			<title>Markdown</title>\
			</head><body>";
	string end = "</body></html>";
	if (fout.is_open()) {
		fout << head << content << end;
		fout.close();
	}
}