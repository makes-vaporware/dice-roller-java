# dice-roller-java

Java parser for standard TTRPG dice expressions (e.g. 2d6 + 1). Runs as a command line program.

Based heavily off [Avrae's d20 parser](https://github.com/avrae/d20), but instead of looking at source code I am stubbornly reverse-engineering it instead

### Disclaimer

(Work in progress. Might not be a comprehensive dice notation parser - I am figuring out the intricacies of the language as we go)

## Usage

```
                    ================================================================
                                            DICE ROLLER HELP
                    ================================================================

                    BASIC SYNTAX:
                      XdY           - Roll X dice with Y sides (e.g. 2d6, 3d20)
                      +, -, *, /    - Standard arithmetic operations
                      ( )           - Parentheses for grouping expressions

                    EXAMPLES:
                      1d20          - Roll a single 20-sided die
                      2d6 + 3       - Roll 2d6 and add 3
                      1d4 + 8d6     - Roll 1d4 and 8d6, then sum together
                      (1d8 + 2) * 2 - Roll 1d8 and add 2, then multiply sum by 2

                    ----------------------------------------------------------------


                    MODIFIERS:
                      mi[X]         - Minimum. Set rolls below X to X. (e.g. 2d6mi3)
                      ma[X]         - Maximum. Set rolls above X to X. (e.g. 2d6ma5)
                      e[X]          - Explode. Roll an additional die when you
                                      match the selector. (e.g. 2d6e6)
                      rr[X]         - Reroll recursively. Rerolls all dice that match the
                                      selector until the selector is no longer fulfilled. (e.g. 1d2rr2)
                      ro[X]         - Reroll once. Rerolls all dice that match the selector
                                      a single time. (e.g.2d2ro1)
                      ra[X]         - Reroll and add. Rerolls up to one die that matches
                                      the selector, then adds it to the total. (e.g. 3d2ra2)
                      k[X]          - Keep. Keeps all dice that match the selector. (e.g. 10d6k3)
                      p[X]          - Drop. Drops all dice that match the selector. (e.g. 10d6p1)

                    Most modifiers can be paired with any one of the selectors below.
                    Minimum and maximum modifiers only work with literal selectors.

                    ----------------------------------------------------------------

                    SELECTORS:
                      hX            - Act on highest X rolls. (e.g. 4d6kh3)
                      lX            - Act on lowest X rolls. (e.g. 1d20kl1)
                      >X            - Act on rolls more than X. (e.g. 6d6k>2)
                      <X            - Act on rolls less than X. (e.g. 3d20k<15)
                      X             - Act on rolls literally matching X. (e.g. 10d6k3)

                    ----------------------------------------------------------------

                    Type 'help' to bring up this guide.
                    Type 'quit' to quit the program.

                    ================================================================
```

## Examples

<img width="1390" height="510" alt="image" src="https://github.com/user-attachments/assets/0348b28f-44d4-403e-9bca-ab059d09325e" />
