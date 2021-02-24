#include<iostream>
#include<fstream>
#include <string>
#include <sstream>
#include "markdown.h"
using namespace std;
int main() {
	markdown A("test.txt");
	A.getfile();
	A.process();
	A.dfsContent(A.getroot());
	A.toHTML();
	return 0;
}
