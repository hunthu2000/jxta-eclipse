package net.osgi.jxse.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

/**
 * A partial property source breaks a parent up in distinct parts, based on the existence of dots (_8) in the given properties
 * TODO: This approach is not very nice yet. Reconsider this idea
 * @author Kees
 *
 * @param <T>
 * @param <U>
 */
public class PartialPropertySource<T extends Object, U extends IJxseDirectives> extends PropertySourceWrapper<T, U> 
implements  IJxseWritePropertySource<T, U>{

	private int offset;
	private String componentName;

	private Collection<IJxsePropertySource<?,?>> children;

	@SuppressWarnings("unchecked")
	protected PartialPropertySource( IJxsePropertySource<T, U> parent, int offset ) {
		super( (IJxsePropertySource<T, U>) selectParent( parent ));
		this.offset = offset;
		children = new ArrayList<IJxsePropertySource<?,?>>();
	}

	public PartialPropertySource( IJxsePropertySource<T, U> parent) {
		this( parent, -1);
	}

	@SuppressWarnings("unchecked")
	PartialPropertySource( String componentName,  IJxsePropertySource<T, U> parent, int offset ) {
		super( (IJxsePropertySource<T, U>) selectParent( parent ));
		this.offset = offset;
		this.componentName = componentName;
		children = new ArrayList<IJxsePropertySource<?,?>>();
	}

	public PartialPropertySource( String componentName,  IJxsePropertySource<T, U> parent) {
		this( componentName, parent, -1 );
	}

	/**
	 * Select the parent of this property source. Always take over the parent of a partial property source
	 * @param parent
	 * @return
	 */
	private static final IJxsePropertySource<?, ?> selectParent( IJxsePropertySource<?, ?> parent ){
		if( parent instanceof PartialPropertySource)
			return (IJxsePropertySource<?, ?>) parent.getParent();
		return parent;
	}

	@Override
	public String getId() {
		return  Utils.isNull( this.componentName )? super.getId(): null;
	}

	@Override
	public String getIdentifier() {
		return  Utils.isNull( this.componentName )? super.getIdentifier(): null;
	}

	@Override
	public IJxsePropertySource<?, ?> getParent() {
		return super.getSource();
	}

	public String getCategory() {
		if( offset < 0 )
			return this.componentName;
		String[] split = this.componentName.split("[.]");
		return split[offset];
	}

	@Override
	public T getIdFromString(String key) {
		String cat = this.getCategory();
		String id = StringStyler.styleToEnum( cat + "." + key.toLowerCase() );
		return (T) super.getSource().getIdFromString( id );
	}

	@Override
	public String getComponentName() {
		String cat = this.getCategory();
		return Utils.isNull(cat)?super.getComponentName(): cat;
	}

	protected int getOffset() {
		return offset;
	}

	
	@Override
	public int getDepth() {
		return super.getDepth() + 1;
	}

	/**
	 * returns true if the given id is valid for this partial property source
	 * @param id
	 * @return
	 */
	protected boolean isValidId( T id ){
		if( id == null )
			return false;
		String check =  id.toString().toLowerCase();
		String cat = this.getCategory();
		if( Utils.isNull( cat )){
			return ( check.indexOf(".") < 0 );
		}else{
			String str = ( cat.toLowerCase() + "." ); 
			return check.startsWith(str);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ManagedProperty<T, Object> getOrCreateManagedProperty(T id, Object value, boolean derived) {
		if(!isValidId(id))
			return null;
		IJxseWritePropertySource<T,U> source = (IJxseWritePropertySource<T, U>) this.getParent();
		ManagedProperty<T, Object> mp = source.getOrCreateManagedProperty(id, value, derived);
		mp.setCategory( this.getCategory());
		return mp;
	}

	@Override
	public ManagedProperty<T, Object> getManagedProperty(T id) {
		if(!isValidId(id))
			return null;
		return super.getManagedProperty(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> propertyIterator() {
		Iterator<T> iterator  = (Iterator<T>) this.getParent().propertyIterator();
		Collection<T> ids = new ArrayList<T>();
		while( iterator.hasNext() ){
			T id = iterator.next();
			if( isValidId(id) )
				ids.add(id);
		}
		return ids.iterator();
	}

	@Override
	public boolean validate(T id, Object value) {
		if(!isValidId(id))
			return false;
		return false;
	}

	@Override
	public Iterator<U> directiveIterator() {
		if( Utils.isNull( this.componentName ))
			return super.directiveIterator();
		Collection<U> col = new ArrayList<U>();
		return col.iterator();
	}

	@SuppressWarnings({ "rawtypes" })
	protected static boolean canExpand( IJxsePropertySource<Enum<?>, IJxseDirectives> source ){
		Iterator<Enum<?>> iterator  = source.propertyIterator();
		while( iterator.hasNext() ){
			Enum id = iterator.next();
			String check =  StringStyler.prettyString( id.name() );
			int index = check.indexOf(".");
			if( index >= 0 )
				return true;
			}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IJxsePropertySource<Enum<?>,IJxseDirectives> expand( IJxsePropertySource<Enum<?>, IJxseDirectives> parent ){
		if((!canExpand( parent ) || ( parent instanceof PartialPropertySource )))
			return parent;
		PartialPropertySource<? extends Enum<?>, IJxseDirectives> root = new PartialPropertySource( parent );
		expand( parent, root );
		return (IJxsePropertySource<Enum<?>, IJxseDirectives>) root;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static void expand( IJxsePropertySource<Enum<?>, IJxseDirectives> parent, PartialPropertySource<? extends Enum<?>, IJxseDirectives> current ){
		Iterator<Enum<?>> iterator  = parent.propertyIterator();
		while( iterator.hasNext() ){
			Enum id = iterator.next();
			int offset = calculateOffset( id );
			if( offset <= current.getOffset() ) 
				continue;
			String ct = getCategory( id, offset );
			if( Utils.isNull( ct ))
				continue;
			if( !hasChildWithName( current, ct )){
				PartialPropertySource child = new PartialPropertySource( id.toString(), parent, current.getOffset() + 1 ); 
				expand( parent, child );
				current.addChild( child);
			}
		}
	}

	/**
	 * Returns true if the given souce has a child with the given name
	 * @param source
	 * @param name
	 * @return
	 */
	protected static boolean hasChildWithName( IJxsePropertySource<? extends Enum<?>, IJxseDirectives> source, String name ){
		for( IJxsePropertySource<?, ?> child: source.getChildren() ){
			if( child.getComponentName().equals( name ))
				return true;
		}
		return false;
		
	}
	
	/**
	 * Calculate the offset for the given id. returns a negative value if no offset exists
	 * @param id
	 * @return
	 */
	protected static int calculateOffset( Enum<?> id ){
		String check =  StringStyler.prettyString( id.name() );
		int index = check.indexOf(".");
		if( index < 0 )
			return index;
		String[] split = check.split("[.]");
		return split.length - 2;
	}

	/**
	 * Calculate the offset for the given id
	 * @param id
	 * @return
	 */
	protected static String getCategory( Enum<?> id, int offset ){
		if( offset < 0 )
			return null;
		String check =  StringStyler.prettyString( id.name() );
		int index = check.indexOf(".");
		if( index < 0 )
			return check;
		String[] split = check.split("[.]");	
		if( offset >= split.length - 1)
			return null;
		return split[ offset ];
	}

	@Override
	public boolean setProperty(T id, Object value) {
		IJxseWritePropertySource<T,U> source = (IJxseWritePropertySource<T, U>) super.getSource();
		return source.setProperty(id, value);
	}

	@Override
	public boolean setDirective(U id, Object value) {
		IJxseWritePropertySource<T,U> source = (IJxseWritePropertySource<T, U>) super.getSource();
		return source.setDirective(id, value);
	}

	public boolean addChild( IJxsePropertySource<?, ?> child ){
		return this.children.add( child );
	}

	public void removeChild( IJxsePropertySource<?, ?> child ){
		this.children.remove( child );
	}

	@Override
	public IJxsePropertySource<?, ?>[] getChildren() {
		return this.children.toArray(new IJxsePropertySource[children.size()]);
	}
}
