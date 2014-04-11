
//Implement mini CC-NUMA architecture

#include "CC_NUMA_Node.h"

// Initialize memory unit contents and statistics.
CC_NUMA::CC_NUMA() {
	int k, j, contents = 5;
	cc = 0;
	for(k=0; k<4; k++) {
		for(j=0; j<16; j++)
			S[k].Mem[j] = contents++; }
	}

// Returns corresponding integer value to requested Instruction Register
// contents. Instruction Register is an operation on the given instruction.
int CC_NUMA::IR(int left, int right) {
	int result = 0, k = left;
	while(k<=right) {
		if( i.at(k) == '1' ) {
			result<<=1;
			result = result|1; }
		else if( i.at(k) == '0' )
			result<<=1;
		else {
			cout<<"\n ERROR\n";
			result = 0;
			k = 36; }
		k++; }
	return result; }

// Decodes the given instruction into instruction sub-fields as integers 
void CC_NUMA::decode() {
	node	= IR(0,1);
	cpu		= IR(2,2);	
	op		= IR(4,9);
	rs		= IR(10,14);
	rt		= IR(15,19);
	rs-=16; rt-=16;
	offs	= IR(20,35); }

// In an cache entry eviction, the directory entry that corresponds to 
// evicted data is checked.  If directory entry is dirty and cache entry
// is valid, memory is updated before data is evicted from cache.
void CC_NUMA::ifEvict() {
	int addr = S[node].C[cpu].H[mod].tag;
	int temp1 = addr>>4, temp2 = addr%16;
	if( S[node].C[cpu].H[mod].valid && S[temp1].Dir[temp2][4] == 2 )
		S[temp1].Mem[temp2] = S[node].C[cpu].H[mod].data; }

// When read miss and directory shows dirty data in memory, desired data
// is fetched from the cache with the current data.
void CC_NUMA::fetchDirty(int global) {
	int smp = global/4, local = global%16, mod = global%4;
	int k, j;
	for(k=0; k<4; k++) { // all smp's
	if( S[smp].Dir[local][k] ) { // if shared in this smp node
		for(j=0; j<2; j++) { // cpu/cache unit
		if( S[k].C[j].H[mod].valid && S[k].C[j].H[mod].tag == global  )
			S[smp].datum = S[k].C[j].H[mod].data; }
		}	}	}

// User requests a load instruction.
void CC_NUMA::lw() {

	// Read hit case in local cache.  Data to register. 1 clock cycle.
	if( S[node].C[cpu].hit(global) ) {
		S[node].C[cpu].P[rt] = S[node].C[cpu].datum;
		cc++; }
	
	// Read hit from the second cache in SMP node. Local cache is updated. 
	// Data to register. 30 clock cycles.
	else if( S[node].C[(cpu+1)%2].hit(global) ) {
		S[node].C[cpu].H[mod].data = S[node].C[(cpu+1)%2].datum;
		S[node].C[cpu].H[mod].tag = global;
		S[node].C[cpu].H[mod].valid = true;

		S[node].C[cpu].P[rt] = S[node].C[(cpu+1)%2].datum;
		cc += 30; }
	
	// Read miss case and home directory indicates data is current
	// in memory.  Check for cache eviction and corresponding operation.
	// Local cache is updated. Data to register. Directory shows shared.
	// 100 clock cycles.
	else if( S[smp].Dir[local][4] != 2 ) {
		ifEvict();
		S[node].C[cpu].H[mod].data = S[smp].Mem[local];
		S[node].C[cpu].H[mod].tag = global;
		S[node].C[cpu].H[mod].valid = true;
		S[node].C[cpu].P[rt] = S[smp].Mem[local];

		S[smp].Dir[local][4] = 1;
		S[smp].Dir[local][node] = 1;
		cc += 100; }
	
	// Read miss case and home directory indicates data is dirty.  Check
	// for cache eviction operation.  Local cache is updated, and data to
	// register.  Home memory is updated and directory shows shared status.
	// 135 clock cycles.
	else {
		ifEvict();
		fetchDirty(global);

		S[node].C[cpu].H[mod].data = S[smp].datum;
		S[node].C[cpu].H[mod].tag = global;
		S[node].C[cpu].H[mod].valid = true;
		S[node].C[cpu].P[rt] = S[node].C[cpu].H[mod].data;

		S[smp].Mem[local] = S[smp].datum;
		S[smp].Dir[local][4] = 1;
		S[smp].Dir[local][node] = 1;
		cc += 135; }
	}

// Invalidates cache entries of shared data when an instruction updates 
// said data.
void CC_NUMA::invalidate(int global) {
	int mod = global%4, k, j;
	for(k=0; k<4; k++) { // smp node
		for(j=0; j<2; j++) { // cpu/cache unit
		if( S[k].C[j].H[mod].tag == global ) // hit
			S[k].C[j].H[mod].valid = false; }
	}	}

// User requests store instruction
void CC_NUMA::sw() {

	// Local SMP cache write hit case.  Cache is updated.  Invalidate other
	// caches with shared memory.  Directory shows dirty memory entry.
	// 1 clock cycle.
	if( S[node].C[cpu].hit(global) ) {
		S[node].C[cpu].H[mod].data = S[node].C[cpu].P[rt];
		invalidate(global);
		S[smp].Dir[local][4] = 2;
		cc++; }
	else if( S[node].C[(cpu+1)%2].hit(global) ) {
		S[node].C[(cpu+1)%2].H[mod].data = S[node].C[cpu].P[rt];
		invalidate(global);
		S[smp].Dir[local][4] = 2;
		cc++; }

	// Cache write miss case. Home memory is updated. 100 clock cycles
	else {
		S[smp].Mem[local] = S[node].C[cpu].P[rt];

		// Directory: shared or dirty status => shared. Invalidate
		// other caches with shared data.
		if( S[smp].Dir[local][node] != 0 ) {
			S[smp].Dir[local][node] == 1;
			invalidate(global); }
		cc += 100;
	}	}

// Decodes and executes user given instructions.  User given address is
// computed and partitioned for interconnected network convenience.
// Only lw and sw are executed.
void CC_NUMA::ExecI() {
	decode();
	global = offs+S[node].C[cpu].P[rs];
	smp = global>>4; local = global%16; mod = global%4;
	if( op == 35 )
		lw();
	else if( op == 43 )
		sw();
	else 
		cout<<"\n ERROR\n"; }

// Prints requested number of binary bits of given integer
void CC_NUMA::bitPrint(int v, int digits) {
	int k;
	for(k=digits-1; k>=0; k--)
		cout<<( v>>k & 1 ); }

// Prints contents of SMP memory unit, corresponding directory, and for
// each CPU/cache unit in a SMP node, prints contents of processor registers
// and cache entry fields.
void CC_NUMA::display() {
	int k, j, l, m, n, p;
	for(k=0; k<4; k++) {
		cout<<"\n\n\n SMP("<<k+1<<"):";
		for(n=0; n<2; n++) {
			cout<<"\n\n   CPU("<<n+1<<"):";
			cout<<"\n\n      Cache #    Valid      Tag";
			cout<<"                      Data\n";
			for(j=0; j<4; j++) {
				cout<<"\n         "; bitPrint(j,2); cout<<"        ";
				cout<<S[k].C[n].H[j].valid<<"       ";
				bitPrint(S[k].C[n].H[j].tag,6); cout<<"       ";
				bitPrint(S[k].C[n].H[j].data,32); }
			}
		for(l=0; l<16; l++) {
			cout<<"\n\n   Memory "; bitPrint((k*16)+l,6);
			cout<<"  = "; bitPrint(S[k].Mem[l],32);
				cout<<"\n      Status       SMP(0)   ";
				cout<<"SMP(1)   SMP(2)   SMP(3)\n";
			if( S[k].Dir[l][4] == 0 )
				cout<<"     Uncached";
			else if( S[k].Dir[l][4] == 1 )
				cout<<"      Shared";
			else
				cout<<"      Dirty";
			for(m=0; m<4; m++)
				cout<<"        "<<S[k].Dir[l][m]; }
	}	}

// Menu gives 3 options.  Execute user given instruction, display contents
// of data structures, and quit with statistics.
void CC_NUMA::menu() {
	int response, IC = 0;
	bool run = true;
	cout<<endl<<"\n\n Mini CC NUMA\n";

	while( run ) {
		cout<<"\n\n\n 1. Instruction execution";
		cout<<"\n 2. Display each node's cache/memory/directory contents";
		cout<<"\n 3. Quit\n ";
		cin>>response; getchar();

		switch(response) {
		case 1:
			cout<<"\n Enter the instruction:  ";
			getline(cin,i);
			ExecI();
			IC++;
			break;
		case 2:
			display();
			break;
		case 3:
			cout<<"\n\n Total Accessing Cost         = "<<cc;
			cout<<"\n Average Cost per Instruction = ";
			cout<<cc/IC<<endl<<endl;
			run = false;
			break;
		default:
			cout<<"\n Invalid input\n";
			break; }
	}	}
