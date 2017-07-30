
package com.tea.common.base.constant;

import com.google.gson.annotations.SerializedName;

public enum Platform {
	@SerializedName("0")
	PC(0,"PC"),
	
	@SerializedName("1")
	Android(1,"Android"),
	
	@SerializedName("2")
	IOS(2,"IOS"),
	
	@SerializedName("3")
	WeiXin(3,"WeiXin"),
	
	@SerializedName("4")
	WAP(4,"WAP"),
	
	@SerializedName("5")
	BACK_PC(5,"BACK_PC");

	private Integer value;

	private String desc;

	private Platform(Integer value,String desc){
		this.value = value;
		this.desc = desc;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static String getNameByValue(int value){
		String name = "";
		if(value== PC.getValue().intValue()){
			name = PC.getDesc();
		}else if(value== Android.getValue().intValue()){
			name = Android.getDesc();
		}else if(value== IOS.getValue().intValue()){
			name = IOS.getDesc();
		}else if(value== WeiXin.getValue().intValue()){
			name = WeiXin.getDesc();
		}else if(value== WAP.getValue().intValue()){
			name = WAP.getDesc();
		}else if(value== BACK_PC.getValue().intValue()){
			name = BACK_PC.getDesc();
		}
		return name;
	}
	
	public static Platform ObjectFromNumber(Number n) {
        switch (n.intValue()) {
            case 0:
                return PC;
            case 1:
                return Android;
            case 2:
                return IOS;
            case 3:
                return WeiXin;
            case 4:
                return WAP;
            case 5:
                return BACK_PC;
        }
        return null;
    }

    public static Number Enum2Number(Platform t) {
        return t.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
