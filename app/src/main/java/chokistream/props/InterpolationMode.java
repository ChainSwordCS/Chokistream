package chokistream.props;

public enum InterpolationMode implements EnumProp {
	NONE("None"),
	SMOOTH("Smooth");
	
	private final String longName;
	
	private InterpolationMode(String name) {
		longName = name;
	}

	@Override
	public String getLongName() {
		return longName;
	}
}
