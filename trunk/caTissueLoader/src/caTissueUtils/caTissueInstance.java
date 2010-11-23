/* THIS caBIGª SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 * WARRANTIES (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY, NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) 
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE
 * OR ITS AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS caBIGª
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package caTissueUtils;
public class caTissueInstance 
{
	private String name;
	private String url;
	private String username;
	private String password;
	
	public caTissueInstance(String name, String url, String username, String password) {
		this.name = name;
		this.url = url;
		this.username = username;
	 	this.password = password;

	}
	
	public String getName() { return name; }
	public String getURL() { return url; }
	public String getUsername() { return username; }
	public String getPassword() { return password; }
}