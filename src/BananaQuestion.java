public class BananaQuestion {
    private final String imageUrl;
    private final int solution;

    public BananaQuestion(String imageUrl, int solution) {
        this.imageUrl = imageUrl;
        this.solution = solution;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getSolution() {
        return solution;
    }
}
