#include <stdio.h>
#include <stdlib.h>
#include <string.h>

FILE *fp;

//structure of a Node

typedef struct MinHeapNode
{
	char data; //contains a character ($ for cluster of characters)
	unsigned freq; //frequency of the character stored in the node
	struct MinHeapNode* left; //left node
	struct MinHeapNode* right; //right node
} MinHeapNode;

//structure of a MinHeap 

typedef struct MinHeap
{
	unsigned size; //current size of the minheap
	unsigned capacity; //capacity of minheap
	struct MinHeapNode** array; //array of type MinHeapNode*
} MinHeap;

//function to create a new node

MinHeapNode* newNode (char data, unsigned freq)
{
	MinHeapNode* temp = (MinHeapNode*)malloc(sizeof(MinHeapNode));
	//assigning values to contents of new node
	temp->left = temp->right = NULL;
	temp->data = data;
	temp->freq = freq;
	return temp;
}

//function to create a new MinHeap

MinHeap* createMinHeap (unsigned capacity)
{
	MinHeap* minHeap = (MinHeap*)malloc(sizeof(MinHeap));
	//assigning values to contents of new MinHeap
	minHeap->size = 0;
	minHeap->capacity = capacity;
	minHeap->array = (MinHeapNode**)malloc(minHeap->capacity*sizeof(MinHeapNode*));
	return minHeap;
}

//function to swap two nodes

void swapMinHeapNode (MinHeapNode** a, MinHeapNode** b) //swap addresses of node pointers
{
	MinHeapNode* t = *a;
    *a = *b;
    *b = t;
}

//function to arrange an array in a MinHeap form

void minHeapify (MinHeap* minHeap, int idx) //for the first call, idx is always 0
{
	int smallest, left, right;
	smallest = idx; //reset
	left = 2 * idx + 1; //left node index has this relation
	right = 2 * idx + 2; //right node index has this relation
	if (left < minHeap->size && minHeap->array[left]->freq < minHeap->array[smallest]->freq)
		smallest=left; //this is done, as it was not following the rule otherwise
	if (right < minHeap->size && minHeap->array[right]->freq < minHeap->array[smallest]->freq)
		smallest=right; //this is done, as it was not following the rule otherwise
	//the rule: children nodes have values larger than parent node
	if (smallest != idx) //meaning nothing needs to be changed!
	{
		swapMinHeapNode ( &minHeap->array[smallest], &minHeap->array[idx] ); //sending addresses of nodes
		minHeapify (minHeap, smallest); //recursive call of the function, till smallest = idx
	}
}

//function to check if size of Huffman tree is 1, i.e. encoding is finished

int isSizeOne(MinHeap* minHeap)
{
	return (minHeap->size == 1); //if 1, returns 1, else returns 0
}

//function to extract the minimum valued node from a MinHeap

MinHeapNode* extractMin (MinHeap* minHeap)
{
	MinHeapNode* temp = minHeap->array[0]; //why? because array[0] contains the least valued element (rule of MinHeap)
	minHeap->array[0] = minHeap->array[minHeap->size-1]; //swapping, as array[0] is not required anymore
	minHeap->size -= 1; //reducing size of minHeap because last element is redundant
	minHeapify(minHeap, 0); //remaining elements are arranged in minHeap
	return temp; //least valued Node is returned
}

//function to insert node named minHeapNode in minHeap

void insertMinHeap (MinHeap* minHeap, MinHeapNode* minHeapNode)
{
	minHeap->size += 1; // incrementing size of minHeap as new node is added
	//bubbling up:
	int i = minHeap->size - 1; //last index, i.e. leaf node
	//why is this done? Parent node has index (i-1)/2. So basically we are swapping with parent when frequency is found to be smaller
	while (i && minHeapNode->freq < minHeap->array[(i-1)/2]->freq) //done till value of the node is larger than that of parent
	{
		//swapping with parent:
		minHeap->array[i] = minHeap->array[(i-1)/2];
		i = (i-1)/2; //current index becomes that of previous parent's index
	}
	minHeap->array[i] = minHeapNode;
}

//function to create and build MinHeap

MinHeap* createAndBuildMinHeap (char data[], int freq[], int size)
{
	int i;
	MinHeap* minHeap = createMinHeap(size);
	for (i = 0; i < size; ++i)
	{
		minHeap->array[i] = newNode(data[i], freq[i]); //inserting data
	}
	minHeap->size = size;
	minHeapify(minHeap, 0);	
	//buildMinHeap(minHeap); //creating a MinHeap out of given data
	return minHeap;
}

//function to check if a node is a leaf node

int isLeaf (MinHeapNode* minHeapNode)
{
	return (!(minHeapNode->left && minHeapNode->right)); //if both are null, returns 1 else 0
}

//function to actually build the Huffman tree

MinHeapNode* buildHuffmanTree (char data[], int freq[], int size)
{
	MinHeapNode* left; MinHeapNode* right; MinHeapNode* top;
	MinHeap* minHeap = createAndBuildMinHeap(data,freq,size);
	while (!isSizeOne(minHeap))
	{
		left = extractMin(minHeap);
		right = extractMin(minHeap);
		top = newNode('$', left->freq + right->freq);
		top->left = left;
		top->right = right;
		insertMinHeap(minHeap,top);
	}
	return extractMin(minHeap);
}

void printArr (int arr[], int n)
{
	int i;
	for (i = 0; i < n; i++)
		fprintf(fp, "%d", arr[i]);
	fprintf(fp, "%c", '\n');
}

void printCodes (MinHeapNode* root, int arr[], int top)
{
	//Assign 0 to left edge
	if (root->left)
	{
		arr[top]=0;
		printCodes(root->left, arr, top+1);
	}
	//Assign 1 to right edge
	if (root->right)
	{
		arr[top]=1;
		printCodes(root->right, arr, top+1);
	}
	if (isLeaf(root))
	{
		fprintf(fp, "%c", root->data);
		printArr(arr, top);
	}
}

void HuffmanCodes(char data[], int freq[], int size)
{
	MinHeapNode* root = buildHuffmanTree(data, freq, size);
	int arr[128], top=0;
	printCodes(root, arr, top);
}

int no_of_distinct_char (char* inp, int inp_l)
{
	int i, j, count;
	char ch;
	count = 0;
	for (i = 0; i < inp_l; i++)
	{
		ch = inp[i];
		if ( (int)ch != 7 )
		{
			count++;
				for (j = i; j < inp_l; j++)
			{
				if (inp[j] == ch)
					inp[j] = (char)(7);
			}
		}
	}
	return count;
}

void createCharFreqArray (char* inp, int inp_l, char* data, int* freq, int distinct)
{
	int i, j, count, fr;
	char ch;
	count = fr = 0;
	for (i = 0; i < inp_l; i++)
	{
		ch = inp[i];
		if ( (int)ch != 7)
		{
			data[count] = ch;
			for (j = i; j < inp_l; j++)
			{
				if (inp[j] == ch)
				{

					fr++;
					inp[j] = (char)(7);
				}
			}
			freq[count++] = fr;
			fr = 0;
		}
	}
}

char* loadFile ()
{
	FILE *F;
	long lSize;
	char *buffer;

	F = fopen ( "source.dat" , "rb" );
	if( !F ) perror("source.dat"),exit(1);

	fseek( F , 0L , SEEK_END);
	lSize = ftell( F );
	rewind( F );

	/* allocate memory for entire content */
	buffer = calloc( 1, lSize+1 );
	if( !buffer ) fclose(F),fputs("memory alloc fails",stderr),exit(1);

	/* copy the file into the buffer */
	if( 1!=fread( buffer , lSize, 1 , F) )
	  fclose(F),free(buffer),fputs("entire read fails",stderr),exit(1);

	fclose(F);

	return buffer;
}

void stringcopy (char* dst, char* src, int length)
{
	int i;
	for (i = 0; i < length; i++)
	{
		dst[i] = src[i];
	}
}

int main (void)
{
	int i;
	fp = fopen ("dictionary.dic", "w");
	if (fp == NULL)
	{
		printf("Error!");
		exit(1);
	}
	char* src = loadFile();
	int inp_l = strlen(src) - 1;
	char* inp = malloc (inp_l); stringcopy(inp,src,inp_l);
	char* inp_c = malloc (inp_l); stringcopy(inp_c,src,inp_l);
	int size = no_of_distinct_char(inp, inp_l);
	char* arr = (char*)malloc(size*sizeof(char));
	int* freq = (int*)malloc(size*sizeof(int));
	createCharFreqArray(inp_c, inp_l, arr, freq, size);
	HuffmanCodes(arr, freq, size);
	fclose(fp);
	free(src);
	return 0;
}
