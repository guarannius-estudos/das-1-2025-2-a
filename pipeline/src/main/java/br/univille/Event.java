package br.univille;

public record Event(int x, int y, int color) {
    public static Event fromJson(String json) {
        String content = json.substring(json.indexOf('[')+1, json.indexOf(']'));
        int x = 0, y = 0, color = 0;        
        String[] fields = content.split(", ");

        for (String field : fields) {
            String[] keyValue = field.split("=");
            
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                
                switch (key) {
                    case "x" -> x = Integer.parseInt(value);
                    case "y" -> y = Integer.parseInt(value);
                    case "color" -> color = Integer.parseInt(value);
                }
            }
        }
        
        return new Event(x, y, color);
    }
}
