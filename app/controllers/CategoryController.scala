package controllers

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.i18n._
import model._
import lib.persistence.onMySQL._
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import CategoryForm._

@Singleton
class CategoryController @Inject()(val controllerComponents: ControllerComponents) extends BaseController  with I18nSupport{

  	def index() = Action.async { implicit request =>
		CategoryRepository.getAll() map { categorys =>
			Ok(views.html.CategoryList(
				ViewValueCategorys(
					categorys = categorys.map(n => n.v),
					title     = "Category list",
					cssSrc    = Seq("main.css","list.css"),
					jsSrc     = Seq("main.js")
				)
			))
		}
  	}

	def addCategoryPage() = Action { implicit request =>
		Ok(views.html.AddCategory(ViewValueCategory(
			title  = "add Category",
			cssSrc = Seq("main.css"),
			jsSrc  = Seq("main.js")
		),form))
	}

	def addCategory() = Action async{ implicit request =>
		form.bindFromRequest().fold({ formWithErrors: Form[Category] =>
			Future{
				BadRequest(views.html.AddCategory(ViewValueCategory(
					title  = "add Category",
					cssSrc = Seq("main.css"),
					jsSrc  = Seq("main.js")
				),formWithErrors))
			}
		}, { c: Category =>
			import lib.model.Category
			import lib.model.Category.Color
			val category = Category.apply(c.name,c.slug,Color(c.color.toShort))

			CategoryRepository.add(category).map( id => {
				Redirect(routes.CategoryController.index)
			})
		})
	}

	def delete(id: Long) = Action.async  { implicit request =>

		val cid   = lib.model.Category.Id(id)
		val noCid = lib.model.Category.Id(0)

		CategoryRepository.get(cid) flatMap { entity =>
			if(entity.isDefined){	
				CategoryRepository.remove(cid) flatMap { c => 
					TodoRepository.getAllByCategory(cid) map { todoList =>
						todoList.map(todo => {
							val newTodo = todo.map(_.copy(category_id = noCid))
							TodoRepository.update(newTodo)
						})
						Redirect(routes.CategoryController.index)
					}
				}
			}else{
				Future{
					Redirect(routes.CategoryController.index)//assume go to error page	
				}
			}
			
		}
	}

	def editCategoryPage(id: Long) = Action.async  { implicit request =>

		CategoryRepository.get(lib.model.Category.Id(id)) map { entity =>
			if(entity.isDefined){
				
				val c          = entity.get.v
				val filledForm = form.fill(Category(id,c.name,c.slug,c.color.code))
				
				Ok(views.html.EditCategory(ViewValueCategory(
					title  = "add Category",
					cssSrc = Seq("main.css"),
					jsSrc  = Seq("main.js")
				),filledForm))
		
			}else{
				Redirect(routes.CategoryController.index)//assume go to error page
			}
		}
	}

	def editCategory() = Action.async  { implicit request =>
		
		form.bindFromRequest().fold({ formWithErrors: Form[Category] =>
			Future{
				BadRequest(views.html.EditCategory(ViewValueCategory(
					title  = "edit Category",
					cssSrc = Seq("main.css"),
					jsSrc  = Seq("main.js")
				),formWithErrors))
			}	
		}, { data: Category =>
			CategoryRepository.get(lib.model.Category.Id(data.id)) flatMap { entity =>
				if(entity.isDefined){
					import lib.model.Category.Color
					val category    = entity.get
					val newCategory = category.map(_.copy(
						name 		= data.name,
						slug        = data.slug,
						color 		= Color(data.color.toShort)))

					CategoryRepository.update(newCategory) map { updateTodo => 
						Redirect(routes.CategoryController.index)
					}	
				}else{
					Future{
						Redirect(routes.CategoryController.index)//assume go to error page
					}
				}
			}
		})
	}
}