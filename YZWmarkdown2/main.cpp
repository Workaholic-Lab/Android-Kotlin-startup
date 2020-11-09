#include<iostream>
#include "markdown.h"
using namespace std;
bool if_para=false;
bool if_ulist=false;
bool if_olist=false;
bool if_quote=false;
bool if_ulist2=false;
bool if_olist2=false;
int main() {
	char input[10];
	cin >> input;
	markdown A(input);
	A.getfile();
	A.process();
	A.toHTML();
	return 0;
}