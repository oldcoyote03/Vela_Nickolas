
#include "CPU_Node.h"

// Initialize processor registers and cache fields
CPU::CPU() {
	int k, j;
	for(k=0; k<2; k++) {
		for(j=0; j<3; j++)
			P[j] = 0; }
	for(k=0; k<4; k++) {
		H[k].valid = false;
		H[k].tag = 0;
		H[k].data = 0; }
	datum = 0; }

// For cache read/write, search in local cache.  If valid and hit, 
// then store data in temporary storage for SMP node to load or store 
// according to given instruction.
bool CPU::hit(int global) {
	bool result = false;
	int mod = global%4;
	if( H[mod].valid && global == H[mod].tag ) {
			datum = H[mod].data;
			result = true; }
	return result; }
