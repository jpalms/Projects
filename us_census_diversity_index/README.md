<h2>U.S. Census Diversity Index</h2>
<p><a name="system"></a></p>
<p>The United States Census Bureau (USCB) estimates the number of persons in each county in each state, categorized by age, gender, race, and other factors. USCB uses six racial categories: White; Black or African American; American Indian or Alaska Native; Asian; Native Hawaiian or Other Pacific Islander; Two or more races.</p>
<p>For example, here are the USCB's population estimates for Monroe County, New York, as of July 1, 2017:</p>
<p></p>
<table border="0" cellpadding="0" cellspacing="0">
<tbody>
<tr>
<td align="left" valign="top">White</td>
<td width="40"></td>
<td align="right" valign="top">573928</td>
</tr>
<tr>
<td align="left" valign="top">Black or African American</td>
<td width="40"></td>
<td align="right" valign="top">121423</td>
</tr>
<tr>
<td align="left" valign="top">American Indian or Alaska Native</td>
<td width="40"></td>
<td align="right" valign="top">3074</td>
</tr>
<tr>
<td align="left" valign="top">Asian</td>
<td width="40"></td>
<td align="right" valign="top">29053</td>
</tr>
<tr>
<td align="left" valign="top">Native Hawaiian or Other Pacific Islander</td>
<td width="40"></td>
<td align="right" valign="top">517</td>
</tr>
<tr>
<td align="left" valign="top">Two or more races</td>
<td width="40"></td>
<td align="right" valign="top">19667</td>
</tr>
<tr>
<td align="left" valign="top">Total</td>
<td width="40"></td>
<td align="right" valign="top">747662</td>
</tr>
</tbody>
</table>
<p></p>
<p>The&nbsp;<strong>diversity index</strong>&nbsp;<em>D</em>&nbsp;for a population is the probability that two randomly chosen individuals in that population will be of different races. The diversity index is calculated with this formula, where&nbsp;<em>N</em><sub><em>i</em></sub>&nbsp;is the number of individuals in racial category&nbsp;<em>i</em>&nbsp;and&nbsp;<em>T</em>&nbsp;is the total number of individuals:</p>
<tr>
<td align="right" valign="center"><em>D</em>&nbsp;&nbsp;=&nbsp;&nbsp;1/<em>T</em><sup>2</sup>&nbsp;</td>
<td align="center" valign="center">&Sigma;</td>
<td align="left" valign="center">&nbsp;<em>N</em><sub><em>i</em></sub>&nbsp;(<em>T</em>&nbsp;&minus;&nbsp;<em>N</em><sub><em>i</em></sub>)</td>
</tr>


#### Note:
Please download the [census dataset](https://www2.census.gov/programs-surveys/popest/datasets/2010-2017/counties/asrh/cc-est2017-alldata.csv) from the USCB web site and place this dataset into the ```dataset``` folder.


# Bug fixes
Note, if you run into problem with `saveAsTextFile` on Windows, you may want to try [these method](https://stackoverflow.com/questions/40764807/null-entry-in-command-string-exception-in-saveastextfile-on-pyspark).
