#include"markdown.h"
#include<fstream>
void markdown::getfile() {
	ifstream fin(mdName);
	string tmp;
	if (fin) {
		while (getline(fin,tmp))
		{
			infile.push_back(tmp);
		}
	}
}
//�����������ݣ������﷨��
void markdown::process() {
	static bool ifstrong = false;
	static bool ifitalic = false;
	int headStart = 0;
	root->child.push_back(new Node(nul));
	int _size = infile.size();
	for (int i = 0;i <_size;i++) {
		headStart = 0;
		ifitalic = false;
		ifstrong = false;
		if (infile[i][0] == '#' && infile[i].size()>1&& infile[i][1] == ' ') {
			root->child.push_back(new Node(headone));
			headStart = 2;
		}
		else if (infile[i][0] == '#' && infile[i].size() > 2 && infile[i][1] == '#'&& infile[i][2]==' ') {
			root->child.push_back(new Node(headtwo));
			headStart = 3;
		}
		else if (infile[i][0] == '#' && infile[i].size() > 3 && infile[i][1] == '#' && infile[i][2] == '#' && infile[i][3] == ' ') {
			root->child.push_back(new Node(headthree));
			headStart = 4;
		}
		if(headStart==0){ 
			root->child.push_back(new Node(nul)); 
		}
		this->process_min(root->child.back(),i,headStart,ifitalic,ifstrong);
		}
		
	}
int markdown::process_min(Node* &root,const int i, int& j, bool& ifitalic, bool& ifstrong) {
	
	int len = infile[i].length();
	for (;j < len;j++) {
		//б��
		if (infile[i][j] == '*' && (j + 1 < infile[i].length() && infile[i][j + 1] != '*' || j + 1 == infile[i].length())) {
			if (!ifitalic) {
				root->child.push_back(new Node(italic));
				j++;
				ifitalic = !ifitalic;
				process_min(root->child.back(),i, j,ifitalic,ifstrong);
			}
			else {
				root->confirm = true;
				return 0;
			}
			continue;
		}
		//����
		else if (infile[i][j] == '*' && j + 1 < infile[i].length() && infile[i][j + 1] == '*') {
			if (!ifstrong) {
				root->child.push_back(new Node(stress));
				j += 2;
				ifstrong = !ifstrong;
				process_min(root->child.back(),i, j, ifitalic, ifstrong);
			}
			else {
				root->confirm = true;
				return 0;
			}
			j++;
			continue;
		}
		else if (infile[i][j] == '\n') {
			root->child.push_back(new Node(para));
			continue;
		}
		//��ͨ�ı�
		else {
			root->child.push_back(new Node(nul));
			root->child.back()->cont += infile[i][j];
		}
	}
}
	
//�õ����ڵ�
Node* markdown:: getroot(){
	return this->root;
}
//���﷨������д��content
void markdown::dfsContent(Node* root) {
	content += frontTag[root->_type];
	content += root->cont;
	for (auto i : root->child) {
		dfsContent(i);
	}
	content += backTag[root->_type];
}

//��content����д��html�ļ�
void markdown::toHTML() {
	ofstream fout("result.html");
	string head="<!DOCTYPE html><html><head>\
			<meta charset=\"utf-8\">\
			<title>Markdown</title>\
			</head><body>";
	string end = "</body></html>";
	if (fout.is_open()) {
		fout << head << content << end;
		fout.close();
	}
}