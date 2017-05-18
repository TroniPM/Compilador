# Compilador

Custom compilator developed in JAVA

# Snnipet!

``` java
main(){
	int function somar1(int a1, int a2){
		return a1 + a2;
	}

	int function somar2(){
		return (5+5);
	}

	void function somar21(boolean a1, boolean a2){
		return a1 || a2;
	}

	boolean function somar3(){
		return true;
	}

	boolean function somar4(boolean a1, int a2){
		return a1;
	}

	int inteiro1;
	boolean boo1;
	int inteiro2;
	boolean boo2;

	inteiro2 = 20;

	inteiro1 = inteiro2 + 2;
	inteiro1 = 2+inteiro2;
	inteiro1 = 2+2;
	inteiro1 = inteiro2+inteiro2;

	inteiro1 = inteiro2 * 2;
	inteiro1 = 2 * inteiro2;
	inteiro1 = 2 * 2;
	inteiro1 = inteiro2 * inteiro2;

	inteiro1 = inteiro2 - 2;
	inteiro1 = 2 - inteiro2;
	inteiro1 = 2 - 2;
	inteiro1 = inteiro2 - inteiro2;

	inteiro1 = inteiro2 / 2;
	inteiro1 = 2 / inteiro2;
	inteiro1 = 2 / 2;
	inteiro1 = inteiro2 / inteiro2;

	inteiro1 = 1 + (1 + 1);
	inteiro1 = inteiro2 + (inteiro2 + inteiro2);
	inteiro1 = inteiro2 + (1 + 2);
	inteiro1 = 1 + (inteiro2 + 2);
	inteiro1 = 1 + (1 + inteiro2);

	inteiro1 = 1 - (1 - 1);
	inteiro1 = inteiro2 - (inteiro2 - inteiro2);
	inteiro1 = inteiro2 - (1 - 2);
	inteiro1 = 1 - (inteiro2 - 2);
	inteiro1 = 1 - (1 - inteiro2);

	inteiro1 = 1 * (1 * 1);
	inteiro1 = inteiro2 * (inteiro2 * inteiro2);
	inteiro1 = inteiro2 * (1 * 2);
	inteiro1 = 1 * (inteiro2 * 2);
	inteiro1 = 1 * (1 * inteiro2);

	inteiro1 = 1 / (1 / 1);
	inteiro1 = inteiro2 / (inteiro2 / inteiro2);
	inteiro1 = inteiro2 / (1 / 2);
	inteiro1 = 1 / (inteiro2 / 2);
	inteiro1 = 1 / (1 / inteiro2);

	boo1 = false;
	boo2 = boo1 && true;
	
	boo1 = true && (true && true);
	boo1 = boo2 && (true && true);
	boo1 = true && (boo2 && true);
	boo1 = true && (true && boo2);
	boo1 = boo2 && (boo2 && boo2);
	
	boo1 = false && (false && false);
	boo1 = boo2 && (false && false);
	boo1 = false && (boo2 && false);
	boo1 = false && (false && boo2);
	boo1 = boo2 && (boo2 && boo2);

	if(boo1==true){
		int x = somar1(inteiro1, inteiro2);
	} else {
	}

	int y = somar2();
	boolean j = somar3();

	print(20);
	
	while(true){
		break;
	}
	
	while(true){
		continue;
	}
	
	while(1+1==2 || 1+1!=2 || 1>2 || 1<2 || 1<=2 || 1>=2){
		break;
	}
	while(j==true || j!=true || j==false || j!=false){
	}
	while(true==true || true!=true || false==false || false!=false){
	}
	while(true==true || true!=true || false==false || false!=false){
	}
	while((true && true)==true && (true&&true)==true){
	}
	while((true && true)==true || (true&&true)==true){
	}
	while((false && false)==false && (false&&false)==false){
	}
	while((false && false)==false || (false&&false)==false){
	}
}
```