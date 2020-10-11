#include<iostream>
#include<vector>
using namespace std;
enum Type { nul,headone,headtwo,headthree,stress, italic,para };
struct Node {
	Type _type;
	vector<Node*>child;
	string cont;
	bool confirm;

	Node(Type type) {
		_type = type;
		confirm = false;
	}
};
const string frontTag[] = { "","<h1>","<h2>" ,"<h3>","<strong>","<em>","<p>"};
const string backTag[] = { "","</h1>","</h2>" ,"</h3>","</strong>","</em>","</p>" };
#pragma once
