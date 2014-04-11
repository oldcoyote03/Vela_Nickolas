/*
Each SMP node contains two CPU/cache units, one memory unit and its
corresponding directory. This unit relies on the interconnected network
to perform work between SMP nodes.
*/

#include "CPU_Node.h"

class SMP
	{
	public:

		SMP();
		CPU C[2]; // 2 CPU and cache units per SMP
		int Mem[16];
		int **Dir;
		int datum; // temp storage for dirty read of directory

	};
