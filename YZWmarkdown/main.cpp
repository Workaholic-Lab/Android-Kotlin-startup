#include<iostream>
#include "markdown.h"
using namespace std;
int main() {
	char input[10];
	cin >> input;
	markdown A(input);
	A.getfile();
	A.process();
	A.dfsContent(A.getroot());
	A.toHTML();
	return 0;
}