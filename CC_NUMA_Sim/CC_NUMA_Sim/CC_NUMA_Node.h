/*
The mini CC_NUMA architecture includes 4 SMP nodes.  This unit represents
both the hardware architecture and the operations performed through the 
inter-connected network that connects the SMP nodes.  This unit only supports
the load and store machine instructions from the MIPS instruction set, and those
operations that support these instructions.
*/

#include "SMP_Node.h"
#include <iostream>
#include <string>
using namespace std;

class CC_NUMA {

	public:
		
		CC_NUMA();
		SMP S[4];
		int IR(int,int);
		void decode();
		void ifEvict();
		void fetchDirty(int);
		void lw();
		void invalidate(int);
		void sw();
		void ExecI();
		void bitPrint(int,int);
		void display();
		void menu();

		int op, rs, rt, offs, node, cpu, cc;
		int global, smp, local, mod;
		string i;

	};
