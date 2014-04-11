
#include "SMP_Node.h"

// Allocate space for directory.  Memory is initialized at the CC-NUMA level.
SMP::SMP() {
	Dir = new int*[16];
	int k, j;
	for(k=0; k<16; k++) {
		Dir[k] = new int[5];
		for(j=0; j<5; j++)
			Dir[k][j] = 0; }
	datum = 0; }
