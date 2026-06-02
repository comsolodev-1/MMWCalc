package com.solodev.mmwcalc.domain.models

object TopicRegistry {

    val FIBONACCI = Topic(
        id = "fibonacci",
        name = "Fibonacci Sequence",
        category = TopicCategory.SEQUENCES,
        description = "Find any term using standard or custom seeds",
        variables = listOf("f1", "f2", "n"),
        variableLabels = mapOf(
            "f1" to "F(1) — First seed",
            "f2" to "F(2) — Second seed",
            "n"  to "n — Position to find"
        ),
        variableHints = mapOf(
            "f1" to "Default: 1",
            "f2" to "Default: 1",
            "n"  to "e.g. 7"
        )
    )

    val ARITHMETIC_SEQUENCE = Topic(
        id = "arithmetic_sequence",
        name = "Arithmetic Sequence",
        category = TopicCategory.SEQUENCES,
        description = "Solve for any variable: a₁, d, n, aₙ, or Sₙ",
        variables = listOf("a1", "d", "n", "an", "sn"),
        variableLabels = mapOf(
            "a1" to "a₁ — First term",
            "d"  to "d — Common difference",
            "n"  to "n — Position / Number of terms",
            "an" to "aₙ — nth term",
            "sn" to "Sₙ — Sum of n terms"
        ),
        variableHints = mapOf(
            "a1" to "e.g. 3",
            "d"  to "e.g. 4",
            "n"  to "e.g. 10",
            "an" to "e.g. 39",
            "sn" to "e.g. 210"
        )
    )

    val GEOMETRIC_SEQUENCE = Topic(
        id = "geometric_sequence",
        name = "Geometric Sequence",
        category = TopicCategory.SEQUENCES,
        description = "Solve for any variable: a₁, r, n, aₙ, or Sₙ",
        variables = listOf("a1", "r", "n", "an", "sn"),
        variableLabels = mapOf(
            "a1" to "a₁ — First term",
            "r"  to "r — Common ratio",
            "n"  to "n — Position / Number of terms",
            "an" to "aₙ — nth term",
            "sn" to "Sₙ — Sum of n terms"
        ),
        variableHints = mapOf(
            "a1" to "e.g. 2",
            "r"  to "e.g. 3",
            "n"  to "e.g. 5",
            "an" to "e.g. 162",
            "sn" to "e.g. 242"
        )
    )

    val CENTRAL_TENDENCY_UNGROUPED = Topic(
        id = "central_tendency_ungrouped",
        name = "Central Tendency (Ungrouped)",
        category = TopicCategory.STATISTICS,
        description = "Mean, median, mode from raw data",
        variables = emptyList(),
        variableLabels = emptyMap(),
        hasDatasetInput = true
    )

    val CENTRAL_TENDENCY_GROUPED = Topic(
        id = "central_tendency_grouped",
        name = "Central Tendency (Grouped)",
        category = TopicCategory.STATISTICS,
        description = "Mean, median, mode from a frequency table",
        variables = emptyList(),
        variableLabels = emptyMap(),
        hasDatasetInput = true
    )

    val DISPERSION_UNGROUPED = Topic(
        id = "dispersion_ungrouped",
        name = "Measures of Dispersion (Ungrouped)",
        category = TopicCategory.STATISTICS,
        description = "Range, variance, standard deviation, IQR",
        variables = emptyList(),
        variableLabels = emptyMap(),
        hasDatasetInput = true
    )

    val DISPERSION_GROUPED = Topic(
        id = "dispersion_grouped",
        name = "Measures of Dispersion (Grouped)",
        category = TopicCategory.STATISTICS,
        description = "Variance and SD from a frequency table",
        variables = emptyList(),
        variableLabels = emptyMap(),
        hasDatasetInput = true
    )

    val RELATIVE_POSITION_UNGROUPED = Topic(
        id = "relative_position_ungrouped",
        name = "Relative Position (Ungrouped)",
        category = TopicCategory.STATISTICS,
        description = "Quartiles, deciles, percentiles, z-score",
        variables = emptyList(),
        variableLabels = emptyMap(),
        hasDatasetInput = true
    )

    val RELATIVE_POSITION_GROUPED = Topic(
        id = "relative_position_grouped",
        name = "Relative Position (Grouped)",
        category = TopicCategory.STATISTICS,
        description = "Qₖ, Dₖ, Pₖ using interpolation formula",
        variables = emptyList(),
        variableLabels = emptyMap(),
        hasDatasetInput = true
    )

    val NORMAL_DISTRIBUTION = Topic(
        id = "normal_distribution",
        name = "Normal Distribution",
        category = TopicCategory.STATISTICS,
        description = "Z-score, raw score, area under the curve",
        variables = listOf("z", "x", "mean", "sd"),
        variableLabels = mapOf(
            "z"    to "z — Z-score",
            "x"    to "x — Raw score",
            "mean" to "μ — Mean",
            "sd"   to "σ — Standard deviation"
        ),
        variableHints = mapOf(
            "z"    to "e.g. 1.5",
            "x"    to "e.g. 85",
            "mean" to "e.g. 75",
            "sd"   to "e.g. 10"
        )
    )

    val LINEAR_REGRESSION = Topic(
        id = "linear_regression",
        name = "Linear Regression",
        category = TopicCategory.STATISTICS,
        description = "Slope, intercept, and predict values from dataset",
        variables = listOf("predict_x", "predict_y"),
        variableLabels = mapOf(
            "predict_x" to "Predict x — given ŷ value",
            "predict_y" to "Predict ŷ — given x value"
        ),
        variableHints = mapOf(
            "predict_x" to "Leave blank to predict x",
            "predict_y" to "Leave blank to predict ŷ"
        ),
        hasDatasetInput = true
    )

    val CORRELATION = Topic(
        id = "correlation",
        name = "Correlation (Pearson's r)",
        category = TopicCategory.STATISTICS,
        description = "Compute r and interpret correlation strength",
        variables = emptyList(),
        variableLabels = emptyMap(),
        hasDatasetInput = true
    )

    val MODULAR_ARITHMETIC = Topic(
        id = "modular_arithmetic",
        name = "Modular Arithmetic",
        category = TopicCategory.NUMBER_THEORY,
        description = "Find remainder, dividend, or modulus",
        variables = listOf("a", "m", "r"),
        variableLabels = mapOf(
            "a" to "a — Dividend",
            "m" to "m — Modulus",
            "r" to "r — Remainder"
        ),
        variableHints = mapOf(
            "a" to "e.g. 17",
            "m" to "e.g. 5",
            "r" to "e.g. 2"
        )
    )

    val ZELLERS_CONGRUENCE = Topic(
        id = "zellers_congruence",
        name = "Zeller's Congruence",
        category = TopicCategory.NUMBER_THEORY,
        description = "Find the day of the week for any date",
        variables = listOf("day", "month", "year"),
        variableLabels = mapOf(
            "day"   to "Day — Day of month",
            "month" to "Month — Month number (1–12)",
            "year"  to "Year — Full year"
        ),
        variableHints = mapOf(
            "day"   to "e.g. 25",
            "month" to "e.g. 12",
            "year"  to "e.g. 2025"
        )
    )

    val SIMPLE_INTEREST = Topic(
        id = "simple_interest",
        name = "Simple Interest",
        category = TopicCategory.FINANCE,
        description = "Solve for P, r, t, I, or maturity value F",
        variables = listOf("p", "r", "t", "i", "f"),
        variableLabels = mapOf(
            "p" to "P — Principal",
            "r" to "r — Annual rate (%)",
            "t" to "t — Time (years)",
            "i" to "I — Interest earned",
            "f" to "F — Maturity value"
        ),
        variableHints = mapOf(
            "p" to "e.g. 10000",
            "r" to "e.g. 5",
            "t" to "e.g. 2",
            "i" to "e.g. 1000",
            "f" to "e.g. 11000"
        )
    )

    val COMPOUND_INTEREST = Topic(
        id = "compound_interest",
        name = "Compound Interest",
        category = TopicCategory.FINANCE,
        description = "Solve for P, j, m, t, or future value F",
        variables = listOf("p", "j", "m", "t", "f"),
        variableLabels = mapOf(
            "p" to "P — Principal",
            "j" to "j — Nominal annual rate (%)",
            "m" to "m — Compounding periods/year",
            "t" to "t — Time (years)",
            "f" to "F — Future value"
        ),
        variableHints = mapOf(
            "p" to "e.g. 10000",
            "j" to "e.g. 6",
            "m" to "e.g. 12",
            "t" to "e.g. 3",
            "f" to "e.g. 11967"
        )
    )

    val STOCKS = Topic(
        id = "stocks",
        name = "Stocks",
        category = TopicCategory.FINANCE,
        description = "Dividend, yield, EPS, P/E ratio, total return",
        variables = emptyList(),
        variableLabels = emptyMap(),
        formulaPicker = true
    )

    val BONDS = Topic(
        id = "bonds",
        name = "Bonds",
        category = TopicCategory.FINANCE,
        description = "Coupon, yield, bond price, total interest",
        variables = emptyList(),
        variableLabels = emptyMap(),
        formulaPicker = true
    )

    val MUTUAL_FUNDS = Topic(
        id = "mutual_funds",
        name = "Mutual Funds",
        category = TopicCategory.FINANCE,
        description = "NAVPS, shares, returns, total value",
        variables = emptyList(),
        variableLabels = emptyMap(),
        formulaPicker = true
    )

    val LOANS = Topic(
        id = "loans",
        name = "Loans (Amortization)",
        category = TopicCategory.FINANCE,
        description = "Monthly payment, total interest, amortization table",
        variables = listOf("p", "r", "n", "m"),
        variableLabels = mapOf(
            "p" to "P — Loan principal",
            "r" to "r — Annual interest rate (%)",
            "n" to "n — Total monthly payments",
            "m" to "M — Monthly payment"
        ),
        variableHints = mapOf(
            "p" to "e.g. 100000",
            "r" to "e.g. 12",
            "n" to "e.g. 24",
            "m" to "e.g. 4707"
        )
    )

    val CREDIT_CARDS = Topic(
        id = "credit_cards",
        name = "Credit Cards",
        category = TopicCategory.FINANCE,
        description = "Monthly interest, minimum payment, payoff schedule",
        variables = listOf("balance", "monthlyRate", "minPaymentPct", "minPaymentFloor"),
        variableLabels = mapOf(
            "balance"         to "Balance — Outstanding balance",
            "monthlyRate"     to "Monthly rate (%)",
            "minPaymentPct"   to "Min. payment (% of balance)",
            "minPaymentFloor" to "Min. payment floor (₱)"
        ),
        variableHints = mapOf(
            "balance"         to "e.g. 20000",
            "monthlyRate"     to "e.g. 2",
            "minPaymentPct"   to "e.g. 2",
            "minPaymentFloor" to "e.g. 500"
        )
    )

    val ALL: List<Topic> = listOf(
        FIBONACCI,
        ARITHMETIC_SEQUENCE,
        GEOMETRIC_SEQUENCE,
        CENTRAL_TENDENCY_UNGROUPED,
        CENTRAL_TENDENCY_GROUPED,
        DISPERSION_UNGROUPED,
        DISPERSION_GROUPED,
        RELATIVE_POSITION_UNGROUPED,
        RELATIVE_POSITION_GROUPED,
        NORMAL_DISTRIBUTION,
        LINEAR_REGRESSION,
        CORRELATION,
        MODULAR_ARITHMETIC,
        ZELLERS_CONGRUENCE,
        SIMPLE_INTEREST,
        COMPOUND_INTEREST,
        STOCKS,
        BONDS,
        MUTUAL_FUNDS,
        LOANS,
        CREDIT_CARDS
    )

    fun findById(id: String): Topic? = ALL.find { it.id == id }

    fun byCategory(category: TopicCategory): List<Topic> =
        ALL.filter { it.category == category }
}