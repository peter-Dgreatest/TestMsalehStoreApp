package com.itcrusaders.msaleh.bioofly;

public class BitmapHeader_8Bit 
{
	public byte BMP_HEADER[] = new byte[1078];
	
	public BitmapHeader_8Bit(int width, int height)
	{
		int tempInt = 0;
		char tempChar = 0;
		int friction = 0;
		
		friction = (4 - width % 4) % 4;
		
		//----- setting File Header -----
		BMP_HEADER[0] = 'B'; //(0x42) bfType('BM')	
		BMP_HEADER[1] = 'M'; //(0x4D)
		
		tempInt = (width + friction) * height + 1078; 
		BMP_HEADER[5] = (byte)(tempInt >> 24);	//bfSize
		BMP_HEADER[4] = (byte)(tempInt >> 16);
		BMP_HEADER[3] = (byte)(tempInt >> 8);
		BMP_HEADER[2] = (byte)tempInt;
		
		BMP_HEADER[6] = 0x00; //bfReserved(not used)
		BMP_HEADER[7] = 0x00;
		BMP_HEADER[8] = 0x00;
		BMP_HEADER[9] = 0x00;
		
		tempInt = 1078;
		BMP_HEADER[13] = (byte)(tempInt >> 24);	//bfOffBits(54)
		BMP_HEADER[12] = (byte)(tempInt >> 16);
		BMP_HEADER[11] = (byte)(tempInt >> 8);
		BMP_HEADER[10] = (byte)tempInt;
		
		//----- setting Image Header -----
		tempInt = 40;
		BMP_HEADER[17] = (byte)(tempInt >> 24);	//biSize(40)
		BMP_HEADER[16] = (byte)(tempInt >> 16);
		BMP_HEADER[15] = (byte)(tempInt >> 8);
		BMP_HEADER[14] = (byte)tempInt;
		
		tempInt = width;
		BMP_HEADER[21] = (byte)(tempInt >> 24);	//biWidth(160)
		BMP_HEADER[20] = (byte)(tempInt >> 16);
		BMP_HEADER[19] = (byte)(tempInt >> 8);
		BMP_HEADER[18] = (byte)tempInt;
		
		tempInt = height;
		BMP_HEADER[25] = (byte)(tempInt >> 24);	//biHeight(240)
		BMP_HEADER[24] = (byte)(tempInt >> 16);
		BMP_HEADER[23] = (byte)(tempInt >> 8);
		BMP_HEADER[22] = (byte)tempInt;
		
		tempChar = 1;
		BMP_HEADER[27] = (byte)(tempChar >> 8); //biPlanes(1)
		BMP_HEADER[26] = (byte)tempChar;
		
		tempChar = 8;
		BMP_HEADER[29] = (byte)(tempChar >> 8); //biBitCount(8)
		BMP_HEADER[28] = (byte)tempChar;
		
		tempInt = 0;
		BMP_HEADER[33] = (byte)(tempInt >> 24);	//biCompression(0)
		BMP_HEADER[32] = (byte)(tempInt >> 16);
		BMP_HEADER[31] = (byte)(tempInt >> 8);
		BMP_HEADER[30] = (byte)tempInt;
		
		tempInt = (width + friction) * height; 
		BMP_HEADER[37] = (byte)(tempInt >> 24);	//biSizeImage
		BMP_HEADER[36] = (byte)(tempInt >> 16);
		BMP_HEADER[35] = (byte)(tempInt >> 8);
		BMP_HEADER[34] = (byte)tempInt;
		
		//tempInt = 4724;
		tempInt = 0;
		BMP_HEADER[41] = (byte)(tempInt >> 24);	//biXPelsPerMeter(0)
		BMP_HEADER[40] = (byte)(tempInt >> 16);
		BMP_HEADER[39] = (byte)(tempInt >> 8);
		BMP_HEADER[38] = (byte)tempInt;
		
		//tempInt = 4724;
		tempInt = 0;
		BMP_HEADER[45] = (byte)(tempInt >> 24);	//biYPelsPerMeter(0)
		BMP_HEADER[44] = (byte)(tempInt >> 16);
		BMP_HEADER[43] = (byte)(tempInt >> 8);
		BMP_HEADER[42] = (byte)tempInt;
		
		tempInt = 0;
		BMP_HEADER[49] = (byte)(tempInt >> 24);	//biClrUsed(0)
		BMP_HEADER[48] = (byte)(tempInt >> 16);
		BMP_HEADER[47] = (byte)(tempInt >> 8);
		BMP_HEADER[46] = (byte)tempInt;
		
		tempInt = 0;
		BMP_HEADER[53] = (byte)(tempInt >> 24);	//biClrImportant(0)
		BMP_HEADER[52] = (byte)(tempInt >> 16);
		BMP_HEADER[51] = (byte)(tempInt >> 8);
		BMP_HEADER[50] = (byte)tempInt;
		
		//----- setting Color Palette -----
		tempInt = 54;
		for(tempChar = 0; tempChar < 256; tempChar ++)
		{
			BMP_HEADER[tempInt + 4 * tempChar] = (byte)tempChar;
			BMP_HEADER[tempInt + 4 * tempChar + 1] = (byte)tempChar;
			BMP_HEADER[tempInt + 4 * tempChar + 2] = (byte)tempChar;
			BMP_HEADER[tempInt + 4 * tempChar + 3] = 0x00;			
		}		
		
	}

}
